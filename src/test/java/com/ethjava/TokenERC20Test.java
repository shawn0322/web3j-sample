package com.ethjava;

import com.alibaba.fastjson.JSONObject;
import com.ethjava.utils.Environment;
import erc20.TokenERC20;
import org.apache.commons.lang.StringUtils;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
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
    static Web3j web3j = Web3j.build(new HttpService(Environment.RPC_URL));

    public static void main(String[] args) throws Exception {
        //部署合约
        TokenERC20 contract = null;
        Credentials credentials = WalletUtils.loadCredentials("gaoxun", "D:/eth-test/wallet/UTC--2018-04-26T08-04-54.362000000Z--59b4a95b9f7e2612f43a6a26e1dcaf1f62cb87dd.json");

        contract = load(web3j,credentials, "0xd0947a6a90aea84a95f1dc132a35259ef17ad839");

        BigInteger bigInteger = contract.balanceOf("0x93bafdf921dded1ac51c9cef6bdbe9b25db396bd").send();
        System.out.println(bigInteger);

    }

    private static TokenERC20 load(Web3j web3j, Credentials credentials, String contractAddress) throws Exception {
        return TokenERC20.load(contractAddress, web3j, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);
    }

    private static TokenERC20 deploy(Web3j web3j, Credentials credentials, String tokenName, String tokenSymbol) throws Exception {
        BigInteger initialSupply = Convert.toWei(BigDecimal.valueOf(1000), Convert.Unit.GWEI).toBigInteger();
        TokenERC20 contract = TokenERC20.deploy(
                web3j, credentials,
                Contract.GAS_PRICE, Contract.GAS_LIMIT,
                initialSupply, tokenName, tokenSymbol, BigInteger.ONE).send();  // constructor params
        return contract;
    }
}