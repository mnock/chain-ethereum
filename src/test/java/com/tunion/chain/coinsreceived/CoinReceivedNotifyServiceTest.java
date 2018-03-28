package com.tunion.chain.coinsreceived;

import com.alibaba.dubbo.config.annotation.Reference;
import com.tunion.cores.BaseTest;
import com.tunion.cores.result.Results;
import com.tunion.cores.utils.CommConstants;
import com.tunion.cores.utils.JacksonUtil;
import com.tunion.dubbo.IService.chainrouter.IDubboChainRouter;
import com.tunion.dubbo.chainrouter.CoinReceivedNotifyService;
import com.tunion.ethereum.service.EthereumService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.utils.Convert;

import java.math.BigInteger;

import static org.web3j.utils.Convert.toWei;

/**
 * Created by Think on 2018/2/27.
 */
public class CoinReceivedNotifyServiceTest extends BaseTest{

    private static Logger logger = LoggerFactory.getLogger(CoinReceivedNotifyServiceTest.class);

    @Reference
    private IDubboChainRouter iDubboChainRouter;

    @Autowired
    private CoinReceivedNotifyService coinReceivedNotifyService;

    @Autowired
    private EthereumService ethereumService;

    @Test
    public void getBalance()
    {

        try {
            BigInteger value = toWei("0.1", Convert.Unit.ETHER).toBigInteger();
            System.err.println(value);

            String eth =Convert.fromWei("100000000000000000", Convert.Unit.ETHER).toPlainString();
            System.err.println(eth);
            Results results = ethereumService.getBalance("");

            logger.info("response data:{}", JacksonUtil.getJackson(results));

        }catch (Exception e)
        {
            logger.error(e.getMessage());
        }
    }

    @Test
    public void notifyCoinRecevied()
    {
        coinReceivedNotifyService.notifyCoinRecevied("mo2hwxU1TwbcrGyYaXTqhpiEpzpuEHdHuP", CommConstants.MANUFACTOR_TYPE.BitCoin.value(),"095296264a4e802c5849cd00a89dcd3ec648a9f6e37814885aa11f354d3050bc","0.01");
    }

}
