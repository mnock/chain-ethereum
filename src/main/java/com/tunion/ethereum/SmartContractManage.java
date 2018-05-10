package com.tunion.ethereum;

import com.tunion.cores.tools.cache.JedisUtils;
import com.tunion.cores.utils.CommConstants;
import com.tunion.ethereum.smartcontract.SuperCoin_sol_SuperToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by Think on 2018/5/10.
 */
@Service
public class SmartContractManage  implements ApplicationListener<ContextRefreshedEvent> {
    private static Logger logger = LoggerFactory.getLogger(SmartContractManage.class);

    private static Web3j web3j = Web3JClient.getClient();

    private SuperCoin_sol_SuperToken superCoinSolSuperToken;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("onApplicationEvent");
        //需要执行的逻辑代码，当spring容器初始化完成后就会执行该方法。
        loadWallet();
    }

    private void loadWallet()
    {
        try {

            String password = JedisUtils.getObjectByRawkey(CommConstants.CHAINROUTER_WALLET_+CommConstants.MANUFACTOR_TYPE.Ethereum.name());
            String source =  JedisUtils.getObjectByRawkey(CommConstants.CHAINROUTER_WALLET_+CommConstants.MANUFACTOR_TYPE.Ethereum.name()+"Source");

            logger.info("Wallet:{}",source);

            Credentials credentials = WalletUtils.loadCredentials(password,source);

            // 加载合约
            BigInteger gasPrice = BigInteger.valueOf(27000000000L);
            BigInteger gasLimit = BigInteger.valueOf(250000);

            superCoinSolSuperToken = SuperCoin_sol_SuperToken.load("0x5FA78f3d0301afE67B5D0B58cE77c227fC72e20B", web3j, credentials, gasPrice, gasLimit);

            //监听交易
            superCoinSolSuperToken.transferEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                    .subscribe(tx -> {
                        String toAddress = tx.to;
                        String fromAddress = tx.from;
                        String txHash = tx.transactionHash;
                        BigInteger txValue = tx.value;
                        logger.info("toAddress:{},fromAddress:{},txHash:{},txValue:{}",toAddress,fromAddress,txHash,txValue);
                    }, Throwable::printStackTrace);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }
    }

    public SuperCoin_sol_SuperToken getSuperCoinSolSuperToken() {
        return superCoinSolSuperToken;
    }

    public void setSuperCoinSolSuperToken(SuperCoin_sol_SuperToken superCoinSolSuperToken) {
        this.superCoinSolSuperToken = superCoinSolSuperToken;
    }
}
