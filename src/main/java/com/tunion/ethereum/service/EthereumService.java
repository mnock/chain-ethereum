package com.tunion.ethereum.service;

import com.tunion.chainrouter.pojo.AddressGroup;
import com.tunion.cores.result.Results;
import com.tunion.cores.tools.cache.JedisUtils;
import com.tunion.cores.utils.CommConstants;
import com.tunion.cores.utils.StringUtil;
import com.tunion.ethereum.EthereumTransactionListener;
import com.tunion.ethereum.ParityClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.parity.Parity;
import org.web3j.protocol.parity.methods.response.ParityAllAccountsInfo;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/**
 * Created by Think on 2018/2/24.
 */
@Service
public class EthereumService {
    private static Logger logger = LoggerFactory.getLogger(EthereumService.class);

    private static Parity parity = ParityClient.getParity();

    @Autowired
    private EthereumTransactionListener ethereumTransactionListener;

    private String createAccount(String accountName,String password,AddressGroup accountInfo){
        try {
            NewAccountIdentifier newAccountIdentifier = parity.personalNewAccount(password).send();
            if(newAccountIdentifier!=null){
                String accountId = newAccountIdentifier.getAccountId();
                //parity.paritySetAccountName(accountId,accountName).send();
//                Map<String,Object> account = new HashMap<String,Object>();
//                account.put(accountId,accountInfo);
//                parity.paritySetAccountMeta(accountId,account);

                return  accountId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private ParityAllAccountsInfo.AccountsInfo getAccountInfo(String accountId){

        try{
            ParityAllAccountsInfo ParityAllAccountsInfo = parity.parityAllAccountsInfo().send();

            return  ParityAllAccountsInfo.getAccountsInfo().get(accountId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //解密钱包
    private Results decryptWallet(String accountId, String password)
    {
        Results results = null;

        try {
            PersonalUnlockAccount personalUnlockAccount = parity.personalUnlockAccount(accountId,password).send();

            if(personalUnlockAccount.getError()==null)
            {
                results = new Results(CommConstants.API_RETURN_STATUS.NORMAL.value(),CommConstants.API_RETURN_STATUS.NORMAL.desc());
            }else{
                results = new Results(CommConstants.API_RETURN_STATUS.ACCOUNT_PASSWORD_ERROR.value(),CommConstants.API_RETURN_STATUS.ACCOUNT_PASSWORD_ERROR.desc(),personalUnlockAccount.getError().getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    private String getAddressesby(String accoutName)
    {
        String address = JedisUtils.getObjectByRawkey(accoutName+CommConstants.MANUFACTOR_TYPE.Ethereum.name());

        return address;
    }

    public Results createAddress(String accoutName) {
        Results results = null;

        String address = getAddressesby(accoutName);
        //检查账号是否存在，如果已经创建过，提示存在，报错
        if(!StringUtil.isNullStr(address))
        {
            return new Results(CommConstants.API_RETURN_STATUS.ACCOUNT_EXIST_ERROR.value(),CommConstants.API_RETURN_STATUS.ACCOUNT_EXIST_ERROR.desc(),address);
        }

        address=createAccount(accoutName,"",null);

        if(StringUtil.isNullStr(address))
        {
            return new Results(CommConstants.API_RETURN_STATUS.ACCOUNT_EXIST_ERROR.value(),CommConstants.API_RETURN_STATUS.ACCOUNT_EXIST_ERROR.desc(),address);
        }

        //放到缓存中，通知时需要获取
        JedisUtils.setObjectByRawkey(CommConstants.MANUFACTOR_TYPE.Ethereum.name()+address,accoutName);

        //放到钱包中
        JedisUtils.setObjectByRawkey(accoutName+CommConstants.MANUFACTOR_TYPE.Ethereum.name(),address);

        if(!StringUtil.isNullStr(address))
        {
            results =new Results(CommConstants.API_RETURN_STATUS.NORMAL.value(),CommConstants.API_RETURN_STATUS.NORMAL.desc(),address);
        }else{
            results =new Results(CommConstants.API_RETURN_STATUS.COMMAND_ERROR.value(),CommConstants.API_RETURN_STATUS.COMMAND_ERROR.desc());
        }

        return results;
    }

    public Results getAddressesbyAccount(String accoutName) {

        Results results = null;

        String address = getAddressesby(accoutName);

        results =new Results(CommConstants.API_RETURN_STATUS.NORMAL.value(),CommConstants.API_RETURN_STATUS.NORMAL.desc(),address);

        return results;
    }

    public Results listAddress() {

        Results results = new Results(CommConstants.API_RETURN_STATUS.NORMAL.value(),CommConstants.API_RETURN_STATUS.NORMAL.desc());

        try{
            List<String> lstAccount= parity.personalListAccounts().send().getAccountIds();
            for(String account:lstAccount)
            {
                logger.info(account);
            }

            results.setData(lstAccount);
        }catch (Exception e){
            e.printStackTrace();
        }

        return results;
    }

    public Results getBalance(String accoutName) {
        Results results = new Results(CommConstants.API_RETURN_STATUS.NORMAL.value(),CommConstants.API_RETURN_STATUS.NORMAL.desc());

        try {
            Set<String> addressSet= JedisUtils.getSetByRawkey(EthereumTransactionListener.COIN_NAME);

            BigInteger sumBalance=new BigInteger("0");

            for(String address:addressSet)
            {
                address=address.substring(EthereumTransactionListener.COIN_NAME.length());
                sumBalance=sumBalance.add(ethereumTransactionListener.getBalance(address));
            }

            String eth =Convert.fromWei(sumBalance.toString(), Convert.Unit.ETHER).toPlainString();

            results.setData(eth);

            logger.info("wallet balance is:{}",eth);
        }catch (Exception e){
            e.printStackTrace();
        }

        return results;
    }

    public Results withdrawalCash(String accoutName, String accountAddress, String txAmount, String txFee, String comment, String commentTo) {

        logger.info("withdrawalCash to address:{} with {} and fee {}",accountAddress,txAmount,txFee);
        Results results = null;

        String accountId = JedisUtils.getObjectByRawkey(CommConstants.CHAINROUTER_+CommConstants.MANUFACTOR_TYPE.Ethereum.name());
        String password = JedisUtils.getObjectByRawkey(CommConstants.CHAINROUTER_WALLET_+CommConstants.MANUFACTOR_TYPE.Ethereum.name());

        BigInteger amount = Convert.toWei(txAmount, Convert.Unit.ETHER).toBigInteger();

        BigDecimal gasPrice=new BigDecimal("10000000000");
        BigDecimal gasLimit = new BigDecimal(24000);

        Transaction transaction = Transaction.createEtherTransaction(accountId,null,gasPrice.toBigInteger(),gasLimit.toBigInteger(),accountAddress,amount);
        try{

            //解密钱包
            results = decryptWallet(accountId,password);

            if(!CommConstants.API_RETURN_STATUS.NORMAL.value().equals(results.getStatus())) {
                return results;
            }

            EthSendTransaction ethSendTransaction =parity.personalSendTransaction(transaction,password).send();
            if(ethSendTransaction.getError()==null){
                String tradeHash = ethSendTransaction.getTransactionHash();
                logger.info("账户:[{}]转账到账户:[{}],交易hash:[{}]",accountId,accountAddress,tradeHash);
                results.setData(tradeHash);
            }else{
                logger.error(ethSendTransaction.getError().getMessage());
            }

        }catch (Exception e){
            logger.error("账户:[{}]交易失败!",accountId,e);
        }

        return results;
    }

    public Results queryTransactions(String accoutName, int transCount) {

        try{
            EthTransaction ethTransaction = parity.ethGetTransactionByHash("").send();
            if(ethTransaction.getError()==null){

            }else{
                logger.error(ethTransaction.getError().getMessage());
            }

        }catch (Exception e){
            logger.error("查询失败!{}",e.getMessage());
        }

        return null;
    }

    public Results queryTransaction(String txid)
    {
        Results results = new Results(CommConstants.API_RETURN_STATUS.NORMAL.value(),CommConstants.API_RETURN_STATUS.NORMAL.desc());

        try{
            EthTransaction ethTransaction = parity.ethGetTransactionByHash(txid).send();
            if(ethTransaction.getError()==null){
                org.web3j.protocol.core.methods.response.Transaction transaction = ethTransaction.getResult();

                results.setData(transaction);
            }else{
                logger.error(ethTransaction.getError().getMessage());
            }

        }catch (Exception e){
            logger.error("查询失败!{}",e.getMessage());
        }

        return results;
    }
}
