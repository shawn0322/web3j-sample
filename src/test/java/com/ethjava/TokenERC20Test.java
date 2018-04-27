package com.ethjava;

import com.alibaba.fastjson.JSONObject;
import com.ethjava.utils.Environment;
import erc20.TokenERC20;
import org.apache.commons.lang.StringUtils;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
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
        balanceOf(web3j,"0x315EDC75A0A1ECdab0D7887A4F17C931D498163f");
        //deploy(web3j,"ERC20-03","ERC20-0");
        //loadAndTransf(web3j,"0x315EDC75A0A1ECdab0D7887A4F17C931D498163f");
        tx(web3j);
    }

    private static void tx(Web3j web3j) throws java.io.IOException {
        EthBlockNumber number = web3j.ethBlockNumber().send();
        System.out.println(number.getBlockNumber());
        for (BigInteger i = BigInteger.ZERO; i.compareTo(number.getBlockNumber()) < 1; i = i.add(BigInteger.ONE)) {
            List<EthBlock.TransactionResult> transactions = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(i), true).send().getBlock().getTransactions();
            transactions.forEach(tx -> {
                EthBlock.TransactionObject tr = (EthBlock.TransactionObject) tx.get();

                System.out.println(JSONObject.toJSON(tr));
                String input = tr.getInput();
                if(StringUtils.isNotEmpty(input) && input.startsWith("0xa9059cbb")){
                    String contractCode = tr.getTo();
                    System.out.println("合约地址:"+contractCode + "\t所在区块:"+ tr.getBlockNumber());
                }
            });
        }
    }


    private static void loadAndTransf(Web3j web3j,String contractAddress) throws Exception {

        Credentials credentials = WalletUtils.loadCredentials("gaoxun", "D:/eth-test/wallet/UTC--2018-04-26T08-04-54.362000000Z--59b4a95b9f7e2612f43a6a26e1dcaf1f62cb87dd.json");
        TokenERC20 contract = TokenERC20.load(contractAddress,web3j, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);

        BigInteger balance1 = contract.balanceOf("0x426da2ac47fea9e5d68526bc6c7fbd449bba3e2a").send();
        System.out.println("余额++:"+balance1);

        TransactionReceipt send = contract.transfer("0x426da2ac47fea9e5d68526bc6c7fbd449bba3e2a", BigInteger.valueOf(1000000L)).send();


        BigInteger balance2 = contract.balanceOf("0x426da2ac47fea9e5d68526bc6c7fbd449bba3e2a").send();
        System.out.println("余额==:"+balance2);

        System.out.println("交易回执"+JSONObject.toJSON(send));
    }

    private static void balanceOf(Web3j web3j,String contractAddress) throws Exception {

        Credentials credentials = WalletUtils.loadCredentials("gaoxun", "D:/eth-test/wallet/UTC--2018-04-26T08-04-54.362000000Z--59b4a95b9f7e2612f43a6a26e1dcaf1f62cb87dd.json");
        TokenERC20 contract = TokenERC20.load(contractAddress,web3j, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);


        BigInteger send = contract.balanceOf("0x426da2ac47fea9e5d68526bc6c7fbd449bba3e2a").send();
        System.out.println(send);

    }

    private static void deploy(Web3j web3j,String tokenName,String tokenSymbol) throws Exception {

        BigInteger initialSupply = Convert.toWei(BigDecimal.valueOf(1000), Convert.Unit.GWEI).toBigInteger();

        Credentials credentials = WalletUtils.loadCredentials("gaoxun", "D:/eth-test/wallet/UTC--2018-04-26T08-04-54.362000000Z--59b4a95b9f7e2612f43a6a26e1dcaf1f62cb87dd.json");
        TokenERC20 contract = TokenERC20.deploy(
                web3j, credentials,
                Contract.GAS_PRICE, Contract.GAS_LIMIT,
                initialSupply,tokenName,tokenSymbol,BigInteger.ONE).send();  // constructor params


        System.out.println(JSONObject.toJSON(contract));
    }
}
