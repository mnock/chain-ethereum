package com.tunion.ethereum;

import com.tunion.cores.tools.cache.JedisUtils;
import com.tunion.cores.utils.CommConstants;
import com.tunion.cores.utils.JacksonUtil;
import com.tunion.cores.utils.StringUtil;
import com.tunion.dubbo.chainrouter.CoinReceivedNotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.parity.Parity;
import org.web3j.utils.Convert;
import rx.Subscription;

import java.math.BigInteger;
import java.util.Set;

/**
 * Created by Think on 2018/2/6.
 */
@Component("ethereumTransactionListener")
public class EthereumTransactionListener implements Runnable{

    private static Logger logger = LoggerFactory.getLogger(EthereumTransactionListener.class);

    private static Web3j web3j = Web3JClient.getClient();

    private static Parity parity = ParityClient.getParity();

    public static String COIN_NAME = CommConstants.MANUFACTOR_TYPE.Ethereum.name();
    public static int COIN_VALUE = CommConstants.MANUFACTOR_TYPE.Ethereum.value();

    @Autowired
    private CoinReceivedNotifyService coinReceivedNotifyService;

    public EthereumTransactionListener()
    {
        try
        {
            Thread ethThrd=new Thread(this);
            ethThrd.start();
        }catch (Exception e)
        {
            logger.error(e.getMessage());
        }
    }

    public void Listening()
    {
        try{

            Subscription subscription = web3j.transactionObservable().subscribe(tx -> {

                String txid = tx.getHash();

                Set<String> addressSet= JedisUtils.getSetByRawkey(COIN_NAME);

                String fromAddress = tx.getFrom();
                String toAddress = tx.getTo();

                boolean sendFlag=false,receiveFlage=false;

                if(addressSet.contains(COIN_NAME+fromAddress))
                {
                    sendFlag = true;
                    //发送
                    logger.info("Send Address:{}",fromAddress);
                }

                if(addressSet.contains(COIN_NAME+toAddress)) {
                    receiveFlage = true;
                    //接受
                    logger.info("Received Address:{}", toAddress);
                }

                if(sendFlag||receiveFlage) {
                    //在测试过程中发现消息可能存在重复的情况，需要进行处理
                    String oldTxid = JedisUtils.getObjectByRawkey(txid);
                    if(!StringUtil.isNullStr(oldTxid))
                    {
                        logger.error("重复的交易消息！交易txid:{}",txid);
                        return;
                    }

                    int timeout = 60*10;//十分钟
                    JedisUtils.setObjectByRawkey(txid,txid,timeout);

                    if(receiveFlage)
                    {
                        coinReceivedNotifyService.notifyCoinRecevied(toAddress, COIN_VALUE,txid, Convert.fromWei(tx.getValue().toString(), Convert.Unit.ETHER).toPlainString());
                    }

                    logger.info("{},{},{},{},{},{},{},{}", fromAddress, toAddress, tx.getRaw(), tx.getGas(), tx.getGasPrice(),tx.getValue(),tx.getBlockHash(),txid);
                }

            }, Throwable::printStackTrace);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public BigInteger getBalance(String address)
    {
        BigInteger bigInteger=new BigInteger("0");
        try {
            EthBlockNumber ethBlockNumber = parity.ethBlockNumber().send();

            DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(ethBlockNumber.getBlockNumber());
            EthGetBalance ethGetBalance =  parity.ethGetBalance(address,defaultBlockParameter).send();
            if(ethGetBalance.getError()==null){
                bigInteger=ethGetBalance.getBalance();
            }

            logger.info("address {} balance is:{}",address,bigInteger);
        }catch (Exception e){
            logger.error("address:{} balance error:{}",address,e.getMessage());
        }

        return bigInteger;
    }

    public static void main(String[] args) {
        String accountId = "0xb04bcd3E2A944a38939FD2dc896Af1aDA27a60df";

        try{
            String accountAddress="0x3277afcd9e91f44bbf9843ab49a48a70e88f2f43";
            String password="lzf19821210";

            String txid ="0xf9f926f5e82dcedd8175ec643e60c9ffd37ecdf2cce09dfe95b104571c0851c5";

            EthTransaction ethTransaction = parity.ethGetTransactionByHash(txid).send();
            if(ethTransaction.getError()==null){
                Transaction transaction = ethTransaction.getResult();
                System.err.println(JacksonUtil.getJackson(transaction));
            }else{
                logger.error(ethTransaction.getError().getMessage());
            }

//            EthGetBalance ethGetBalance1 = web3j.ethGetBalance(accountId, DefaultBlockParameter.valueOf("latest")).send();
//
//            System.err.println(ethGetBalance1.getBalance());
//
//            BigDecimal amount=new BigDecimal("1000000000000000000");
//            BigDecimal gasPrice=new BigDecimal("20000000000");
//            BigDecimal gasLimit = new BigDecimal(24000);
//
//            BigInteger value = Convert.toWei("1", Convert.Unit.ETHER).toBigInteger();
//            System.err.println(value);
//
//            Transaction transaction = Transaction.createEtherTransaction(accountId,null,gasPrice.toBigInteger(),gasLimit.toBigInteger(),accountAddress,amount.toBigInteger());
//            try{
//
//                PersonalUnlockAccount personalUnlockAccount = parity.personalUnlockAccount(accountId,password).send();
//
//                EthSendTransaction ethSendTransaction =parity.ethSendTransaction(transaction).send();
//
//                if(ethSendTransaction.getError()==null){
//                    String transactionHash = ethSendTransaction.getTransactionHash();
//                    logger.info("账户:[{}]转账到账户:[{}],交易hash:[{}]",accountId,accountAddress,transactionHash);
//
//                }else{
//                    System.err.println(ethSendTransaction.getError().getMessage());
//                }
//
//            }catch (Exception e){
//                logger.error("账户:[{}]交易失败!",accountId,e);
//            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            logger.info("running......");

            Listening();

            while (true) {
                Thread.sleep(20);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
