package com.tunion.ethereum;

import com.tunion.cores.utils.PropertyReader;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * Created by Think on 2018/2/5.
 */
public class Web3JClient {
    private static String ip = PropertyReader.getProperties("ethServerUrl");

    private Web3JClient(){}

    private volatile static Web3j web3j;

    public static Web3j getClient(){
        if(web3j==null){
            synchronized (Web3JClient.class){
                if(web3j==null){
                    web3j = Web3j.build(new HttpService(ip));
                }
            }
        }
        return web3j;
    }
}
