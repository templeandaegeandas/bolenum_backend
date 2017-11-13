package com.bolenum.util;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

import rx.Observable;
import rx.functions.Func1;

/**
 * Auto generated code.<br>
 * <strong>Do not modify!</strong><br>
 * Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>, or {@link org.web3j.codegen.SolidityFunctionWrapperGenerator} to update.
 *
 * <p>Generated with web3j version 2.3.1.
 */
public final class Erc20TokenWrapper extends Contract {
	
	private static final String BINARY = "0x60a0604052600960608190527f546f6b656e20302e310000000000000000000000000000000000000000000000608090815261003e91600091906100d9565b50341561004757fe5b604051610cd3380380610cd3833981016040908152815160208301519183015160608401519193928301929091015b600160a060020a0333166000908152600560209081526040909120859055600485905583516100ab91600191908601906100d9565b5080516100bf9060029060208401906100d9565b506003805460ff191660ff84161790555b50505050610179565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061011a57805160ff1916838001178555610147565b82800160010185558215610147579182015b8281111561014757825182559160200191906001019061012c565b5b50610154929150610158565b5090565b61017691905b80821115610154576000815560010161015e565b5090565b90565b610b4b806101886000396000f300606060405236156100c25763ffffffff7c010000000000000000000000000000000000000000000000000000000060003504166306fdde0381146100c4578063095ea7b31461015457806318160ddd1461018757806323b872dd146101a9578063313ce567146101e257806342966c68146102085780635a3b7e421461022f57806370a08231146102bf57806379cc6790146102ed57806395d89b4114610320578063a9059cbb146103b0578063cae9ca51146103d1578063dd62ed3e14610448575bfe5b34156100cc57fe5b6100d461047c565b60408051602080825283518183015283519192839290830191850190808383821561011a575b80518252602083111561011a57601f1990920191602091820191016100fa565b505050905090810190601f1680156101465780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561015c57fe5b610173600160a060020a0360043516602435610509565b604080519115158252519081900360200190f35b341561018f57fe5b61019761053a565b60408051918252519081900360200190f35b34156101b157fe5b610173600160a060020a0360043581169060243516604435610540565b604080519115158252519081900360200190f35b34156101ea57fe5b6101f2610667565b6040805160ff9092168252519081900360200190f35b341561021057fe5b610173600435610670565b604080519115158252519081900360200190f35b341561023757fe5b6100d46106fd565b60408051602080825283518183015283519192839290830191850190808383821561011a575b80518252602083111561011a57601f1990920191602091820191016100fa565b505050905090810190601f1680156101465780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156102c757fe5b610197600160a060020a036004351661078b565b60408051918252519081900360200190f35b34156102f557fe5b610173600160a060020a03600435166024356107aa565b604080519115158252519081900360200190f35b341561032857fe5b6100d461086c565b60408051602080825283518183015283519192839290830191850190808383821561011a575b80518252602083111561011a57601f1990920191602091820191016100fa565b505050905090810190601f1680156101465780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156103b857fe5b6103cf600160a060020a03600435166024356108f7565b005b34156103d957fe5b604080516020600460443581810135601f8101849004840285018401909552848452610173948235600160a060020a03169460248035956064949293919092019181908401838280828437509496506109c895505050505050565b604080519115158252519081900360200190f35b341561045057fe5b610197600160a060020a0360043581169060243516610b02565b60408051918252519081900360200190f35b60018054604080516020600284861615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156105015780601f106104d657610100808354040283529160200191610501565b820191906000526020600020905b8154815290600101906020018083116104e457829003601f168201915b505050505081565b600160a060020a03338116600090815260066020908152604080832093861683529290522081905560015b92915050565b60045481565b6000600160a060020a03831615156105585760006000fd5b600160a060020a0384166000908152600560205260409020548290101561057f5760006000fd5b600160a060020a03831660009081526005602052604090205482810110156105a75760006000fd5b600160a060020a03808516600090815260066020908152604080832033909416835292905220548211156105db5760006000fd5b600160a060020a03808516600081815260056020908152604080832080548890039055878516808452818420805489019055848452600683528184203390961684529482529182902080548790039055815186815291517fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9281900390910190a35060015b9392505050565b60035460ff1681565b600160a060020a033316600090815260056020526040812054829010156106975760006000fd5b600160a060020a03331660008181526005602090815260409182902080548690039055600480548690039055815185815291517fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca59281900390910190a25060015b919050565b6000805460408051602060026001851615610100026000190190941693909304601f810184900484028201840190925281815292918301828280156105015780601f106104d657610100808354040283529160200191610501565b820191906000526020600020905b8154815290600101906020018083116104e457829003601f168201915b505050505081565b600160a060020a0381166000908152600560205260409020545b919050565b600160a060020a038216600090815260056020526040812054829010156107d15760006000fd5b600160a060020a03808416600090815260066020908152604080832033909416835292905220548211156108055760006000fd5b600160a060020a03831660008181526005602090815260409182902080548690039055600480548690039055815185815291517fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca59281900390910190a25060015b92915050565b6002805460408051602060018416156101000260001901909316849004601f810184900484028201840190925281815292918301828280156105015780601f106104d657610100808354040283529160200191610501565b820191906000526020600020905b8154815290600101906020018083116104e457829003601f168201915b505050505081565b600160a060020a038216151561090d5760006000fd5b600160a060020a033316600090815260056020526040902054819010156109345760006000fd5b600160a060020a038216600090815260056020526040902054818101101561095c5760006000fd5b600160a060020a03338116600081815260056020908152604080832080548790039055938616808352918490208054860190558351858152935191937fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef929081900390910190a35b5050565b6000836109d58185610509565b15610af95780600160a060020a0316638f4ffcb1338630876040518563ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004018085600160a060020a0316600160a060020a0316815260200184815260200183600160a060020a0316600160a060020a0316815260200180602001828103825283818151815260200191508051906020019080838360008314610a99575b805182526020831115610a9957601f199092019160209182019101610a79565b505050905090810190601f168015610ac55780820380516001836020036101000a031916815260200191505b5095505050505050600060405180830381600087803b1515610ae357fe5b6102c65a03f11515610af157fe5b505050600191505b5b509392505050565b6006602090815260009283526040808420909152908252902054815600a165627a7a72305820144dca4ba4580a0797c3c56af58e22596228d84cdb0965ea888eaba4913cea1200290000000000000000000000000000000000000000000000000000e35fa931a0000000000000000000000000000000000000000000000000000000000000000080000000000000000000000000000000000000000000000000000000000000000800000000000000000000000000000000000000000000000000000000000000c0000000000000000000000000000000000000000000000000000000000000000c42656c7269756d546f6b656e0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000342656c0000000000000000000000000000000000000000000000000000000000";
    
    private Erc20TokenWrapper(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
    	super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    private Erc20TokenWrapper(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Transfer", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse._from = (Address) eventValues.getIndexedValues().get(0);
            typedResponse._to = (Address) eventValues.getIndexedValues().get(1);
            typedResponse._value = (Uint256) eventValues.getNonIndexedValues().get(0);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
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
                typedResponse._from = (Address) eventValues.getIndexedValues().get(0);
                typedResponse._to = (Address) eventValues.getIndexedValues().get(1);
                typedResponse._value = (Uint256) eventValues.getNonIndexedValues().get(0);
                typedResponse._transactionHash = log.getTransactionHash();
                return typedResponse;
            }
        });
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Approval", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse._owner = (Address) eventValues.getIndexedValues().get(0);
            typedResponse._spender = (Address) eventValues.getIndexedValues().get(1);
            typedResponse._value = (Uint256) eventValues.getNonIndexedValues().get(0);
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ApprovalEventResponse> approvalEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Approval", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse._owner = (Address) eventValues.getIndexedValues().get(0);
                typedResponse._spender = (Address) eventValues.getIndexedValues().get(1);
                typedResponse._value = (Uint256) eventValues.getNonIndexedValues().get(0);
                typedResponse._transactionHash = log.getTransactionHash();
                return typedResponse;
            }
        });
    }

    public Future<Utf8String> name() throws IOException {
        Function function = new Function("name", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallSingleValueReturn(function);
    }

    public TransactionReceipt approve(Address _spender, Uint256 _amount) throws IOException, TransactionException {
        Function function = new Function("approve", Arrays.<Type>asList(_spender, _amount), Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public Future<Uint256> totalSupply() throws IOException {
        Function function = new Function("totalSupply", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallSingleValueReturn(function);
    }

    public TransactionReceipt transferFrom(Address _from, Address _to, Uint256 _amount) throws IOException, TransactionException {
        Function function = new Function("transferFrom", Arrays.<Type>asList(_from, _to, _amount), Collections.<TypeReference<?>>emptyList());
        return  executeTransaction(function);
    }

    public Future<Uint8> decimals() throws IOException {
        Function function = new Function("decimals", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeCallSingleValueReturn(function);
    }

    public Uint256 balanceOf(Address _owner) throws IOException {
        Function function = new Function("balanceOf", 
                Arrays.<Type>asList(_owner), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallSingleValueReturn(function);
    }

    public Future<Address> owner() throws IOException {
        Function function = new Function("owner", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeCallSingleValueReturn(function);
    }

    public Future<Utf8String> symbol() throws IOException {
        Function function = new Function("symbol", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallSingleValueReturn(function);
    }

    public TransactionReceipt transfer(Address _to, Uint256 _amount) throws IOException, TransactionException {
        Function function = new Function("transfer", Arrays.<Type>asList(_to, _amount), Collections.<TypeReference<?>>emptyList());
        return executeTransaction(function);
    }

    public Future<Uint256> allowance(Address _owner, Address _spender) throws IOException {
        Function function = new Function("allowance", 
                Arrays.<Type>asList(_owner, _spender), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeCallSingleValueReturn(function);
    }

    public static RemoteCall<Erc20TokenWrapper> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue, Uint256 totalSupply, Utf8String tokenName, Uint8 decimalUnits, Utf8String tokenSymbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(totalSupply, tokenName, decimalUnits, tokenSymbol));
        return deployRemoteCall(Erc20TokenWrapper.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor, initialWeiValue);
    }

    public static RemoteCall<Erc20TokenWrapper> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue, Uint256 totalSupply, Utf8String tokenName, Uint8 decimalUnits, Utf8String tokenSymbol) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(totalSupply, tokenName, decimalUnits, tokenSymbol));
        return deployRemoteCall(Erc20TokenWrapper.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor, initialWeiValue);
    }

    public static Erc20TokenWrapper load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
    	return new Erc20TokenWrapper(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static Erc20TokenWrapper load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
    	return new Erc20TokenWrapper(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class TransferEventResponse {
        public Address _from;

        public Address _to;

        public Uint256 _value;
        
        public String _transactionHash;
    }

    public static class ApprovalEventResponse {
        public Address _owner;

        public Address _spender;

        public Uint256 _value;
        
        public String _transactionHash;
    }
}
