package com.ethjava;

import com.alibaba.fastjson.JSONObject;
import com.ethjava.utils.Environment;
import com.fasterxml.jackson.databind.util.JSONPObject;
import erc20.TokenERC20;
import jdk.nashorn.internal.runtime.JSONFunctions;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by Administrator on 2018/4/12.
 */
public class TokenERC20Test {

    public static void main(String[] args) throws Exception {

        Web3j web3j = Web3j.build(new HttpService(Environment.RPC_URL));


        //load(web3j);
        tx(web3j);
    }

    private static void tx(Web3j web3j) throws java.io.IOException {
        EthBlockNumber number = web3j.ethBlockNumber().send();
        System.out.println(number.getBlockNumber());

        for (BigInteger i = BigInteger.ZERO; i.compareTo(number.getBlockNumber()) < 1; i = i.add(BigInteger.ONE)) {

            List<EthBlock.TransactionResult> transactions = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(i), true).send().getBlock().getTransactions();


            transactions.forEach(tx -> {
                EthBlock.TransactionResult tr = (EthBlock.TransactionResult) tx.get();
                Object o = tx.get();
                System.out.println("==="+ JSONObject.toJSON(o));
            });
        }
    }

    /*
    0x59b4a95b9f7e2612f43a6a26e1dcaf1f62cb87dd
UTC--2018-04-26T08-04-54.362000000Z--59b4a95b9f7e2612f43a6a26e1dcaf1f62cb87dd.json
     */
//1.220951596568e+21

    private static void load(Web3j web3j) throws Exception {

        Credentials credentials = WalletUtils.loadCredentials("gaoxun", "D:/eth-test/wallet/UTC--2018-04-26T08-04-54.362000000Z--59b4a95b9f7e2612f43a6a26e1dcaf1f62cb87dd.json");
        TokenERC20 contract = TokenERC20.load("0x4e8d37d9ba3f324b72bbb8049596ee8990ed01a9",web3j, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);


        TransactionReceipt send = contract.transfer("0x426da2ac47fea9e5d68526bc6c7fbd449bba3e2a", BigInteger.valueOf(1000000L)).send();
        System.out.println(JSONObject.toJSON(send));
    }

    private static void deploy(Web3j web3j) throws Exception {

        BigInteger initialSupply = Convert.toWei(BigDecimal.valueOf(1000), Convert.Unit.GWEI).toBigInteger();

        Credentials credentials = WalletUtils.loadCredentials("gaoxun", "D:/eth-test/wallet/UTC--2018-04-26T08-04-54.362000000Z--59b4a95b9f7e2612f43a6a26e1dcaf1f62cb87dd.json");
        TokenERC20 contract = TokenERC20.deploy(
                web3j, credentials,
                Contract.GAS_PRICE, Contract.GAS_LIMIT,
                initialSupply,"ERC20-01","ERC20-01",BigInteger.ONE).send();  // constructor params


        System.out.println(JSONObject.toJSON(contract));
    }

    //0x4e8d37d9ba3f324b72bbb8049596ee8990ed01a9
}
