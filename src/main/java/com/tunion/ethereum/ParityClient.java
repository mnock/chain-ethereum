package com.tunion.ethereum;

import com.tunion.cores.utils.PropertyReader;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.parity.Parity;

/**
 * Created by Think on 2018/2/5.
 */
public class ParityClient {
    private static String ip = PropertyReader.getProperties("ethServerUrl");

    private ParityClient(){}

    private static class ClientHolder{
        private static final Parity parity = Parity.build(new HttpService(ip));
    }

    public static final  Parity getParity(){
        return ClientHolder.parity;
    }
}
