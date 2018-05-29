package com.tunion.ethereum.smartcontract;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.2.0.
 */
public class SuperCoin_sol_SuperToken extends Contract {

    private static Logger logger = LoggerFactory.getLogger(SuperCoin_sol_SuperToken.class);

//    private static final String BINARY = "6060604052341561000f57600080fd5b604051610530380380610530833981016040528080519190602001805182019190602001805182019190602001805191505083151561005457678ac7230489e8000093505b600160a060020a03331660009081526003602052604081208590558380516100809291602001906100b2565b5060018280516100949291602001906100b2565b506002805460ff191660ff929092169190911790555061014d915050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100f357805160ff1916838001178555610120565b82800160010185558215610120579182015b82811115610120578251825591602001919060010190610105565b5061012c929150610130565b5090565b61014a91905b8082111561012c5760008155600101610136565b90565b6103d48061015c6000396000f30060606040526004361061006c5763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166306fdde038114610071578063313ce567146100fb57806370a082311461012457806395d89b4114610162578063a9059cbb14610175575b600080fd5b341561007c57600080fd5b6100846101a6565b60405160208082528190810183818151815260200191508051906020019080838360005b838110156100c05780820151838201526020016100a8565b50505050905090810190601f1680156100ed5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561010657600080fd5b61010e610244565b60405160ff909116815260200160405180910390f35b341561012f57600080fd5b61015073ffffffffffffffffffffffffffffffffffffffff6004351661024d565b60405190815260200160405180910390f35b341561016d57600080fd5b61008461025f565b341561018057600080fd5b6101a473ffffffffffffffffffffffffffffffffffffffff600435166024356102ca565b005b60008054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561023c5780601f106102115761010080835404028352916020019161023c565b820191906000526020600020905b81548152906001019060200180831161021f57829003601f168201915b505050505081565b60025460ff1681565b60036020526000908152604090205481565b60018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561023c5780601f106102115761010080835404028352916020019161023c565b73ffffffffffffffffffffffffffffffffffffffff3316600090815260036020526040902054819010156102fd576103a4565b73ffffffffffffffffffffffffffffffffffffffff82166000908152600360205260409020548181011015610331576103a4565b73ffffffffffffffffffffffffffffffffffffffff3381166000818152600360205260408082208054869003905592851680825290839020805485019055917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9084905190815260200160405180910390a35b50505600a165627a7a723058204c9b9b3ccb265eb70428664c7ce52d394136b46f778ac61137cbfda91acbab450029";

    protected SuperCoin_sol_SuperToken(String contractBinary, String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(contractBinary, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected SuperCoin_sol_SuperToken(String contractBinary,String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(contractBinary, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {

        logger.info("getTransferEvents");
        final Event event = new Event("Transfer", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        logger.info("transferEventObservable");
        final Event event = new Event("Transfer",
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.transactionHash = log.getTransactionHash();
                return typedResponse;
            }
        });
    }

    public RemoteCall<String> name() {
        Function function = new Function("name", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> decimals() {
        Function function = new Function("decimals", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> balanceOf(String param0) {
        Function function = new Function("balanceOf", 
                Arrays.<Type>asList(new Address(param0)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> symbol() {
        Function function = new Function("symbol", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> transfer(String _to, BigInteger _value) {
        Function function = new Function(
                "transfer", 
                Arrays.<Type>asList(new Address(_to),
                new Uint256(_value)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<SuperCoin_sol_SuperToken> deploy(String contractBinary,Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger _supply, String _name, String _symbol, BigInteger _decimals) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Uint256(_supply),
                new Utf8String(_name),
                new Utf8String(_symbol),
                new Uint8(_decimals)));
        return deployRemoteCall(SuperCoin_sol_SuperToken.class, web3j, credentials, gasPrice, gasLimit, contractBinary, encodedConstructor);
    }

    public static RemoteCall<SuperCoin_sol_SuperToken> deploy(String contractBinary,Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger _supply, String _name, String _symbol, BigInteger _decimals) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Uint256(_supply),
                new Utf8String(_name),
                new Utf8String(_symbol),
                new Uint8(_decimals)));
        return deployRemoteCall(SuperCoin_sol_SuperToken.class, web3j, transactionManager, gasPrice, gasLimit, contractBinary, encodedConstructor);
    }

    public static SuperCoin_sol_SuperToken load(String contractBinary,String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new SuperCoin_sol_SuperToken(contractBinary,contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static SuperCoin_sol_SuperToken load(String contractBinary,String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new SuperCoin_sol_SuperToken(contractBinary,contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class TransferEventResponse {
        public String from;

        public String to;

        public BigInteger value;

        public String transactionHash;
    }
}
