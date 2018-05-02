package com.ethjava;

import erc20.DSToken;
import org.web3j.abi.datatypes.Bytes;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;

import java.io.File;
import java.math.BigInteger;

import static com.ethjava.utils.Environment.RPC_URL;
import static org.web3j.tx.ManagedTransaction.GAS_PRICE;

public class DSTokenTest {

    public static void main(String[] args) throws Exception {

        Web3j web3j = Web3j.build(new HttpService(RPC_URL));
        Credentials credentials = WalletUtils.loadCredentials("gaoxun",new File("./UTC--2018-05-02T15-24-30.922000000Z--c26aac60846f9df581fd7573fb90b38b08438990.json"));

        //DSToken dsToken = DSToken.deploy(web3j,credentials,GAS_PRICE,Contract.GAS_LIMIT, symbol_).send();
       // BigInteger balance = dsToken.balanceOf("0xc26aac60846f9df581fd7573fb90b38b08438990").send();

       // System.out.println(balance);

    }
}
