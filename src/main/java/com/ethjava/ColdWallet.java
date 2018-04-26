package com.ethjava;

import com.ethjava.utils.Environment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ChainId;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;

/**
 * 冷钱包
 * 账号 交易相关
 */
public class ColdWallet {

	private static Web3j web3j;

	private static String d = "D:/eth-test/wallet";

	private static String address = "0x2b40d227806cc2c2b638a06337fa09e25d076778";

	public static String keystore = "{\"address\":\"2b40d227806cc2c2b638a06337fa09e25d076778\",\"id\":\"511ea14e-f3aa-4cd2-b579-2f3e660a2fb7\",\"version\":3,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"b412f745e86b6d0f2e530c917949b8987d0eabdfd0ad3fd8680c1be39cd36471\",\"cipherparams\":{\"iv\":\"69968168a62a61fcbbb9d7f933ed0e47\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"d53b22ca1448d6a165c803c16bafd3ce394d702b48762bf0985fdd14eba5f2a2\"},\"mac\":\"cdcd91d6e0baafcc74974cc233677c29414b77364e45f11dd137ae8b41b38405\"}}\n";

	private static String privateKey = "65606054f8eea93ccb125d51e130bb26f2146d753e53eed9a804e9039cb0e37c";

	public static void main(String[] args) {
		web3j = Web3j.build(new HttpService(Environment.RPC_URL));
		try {
			createWallet("gaoxun");
//			decryptWallet(keystore, "gaoxun");
	//		testTransaction();
//			testTokenTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void testTransaction() {
		BigInteger nonce;
		EthGetTransactionCount ethGetTransactionCount = null;
		try {
			ethGetTransactionCount = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING).send();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (ethGetTransactionCount == null) return;
		nonce = ethGetTransactionCount.getTransactionCount();
		BigInteger gasPrice = Convert.toWei(BigDecimal.valueOf(3), Convert.Unit.GWEI).toBigInteger();
		BigInteger gasLimit = BigInteger.valueOf(30000);
		String to = "0x267c1c14abe232ed8704c2a1796d66d98b29fcd3".toLowerCase();
		BigInteger value = Convert.toWei(BigDecimal.valueOf(0.5), Convert.Unit.ETHER).toBigInteger();
		String data = "";
		byte chainId = ChainId.ROPSTEN;//测试网络
		String privateKey = ColdWallet.privateKey;
		String signedData;
		try {
			signedData = signTransaction(nonce, gasPrice, gasLimit, to, value, data, chainId, privateKey);
			if (signedData != null) {
				EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedData).send();
				System.out.println(ethSendTransaction.getTransactionHash());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void testTokenTransaction(Web3j web3j, String fromAddress, String privateKey, String contractAddress, String toAddress, double amount, int decimals) {
		BigInteger nonce;
		EthGetTransactionCount ethGetTransactionCount = null;
		try {
			ethGetTransactionCount = web3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.PENDING).send();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (ethGetTransactionCount == null) return;
		nonce = ethGetTransactionCount.getTransactionCount();
		System.out.println("nonce " + nonce);
		BigInteger gasPrice = Convert.toWei(BigDecimal.valueOf(3), Convert.Unit.GWEI).toBigInteger();
		BigInteger gasLimit = BigInteger.valueOf(60000);
		BigInteger value = BigInteger.ZERO;
		//token转账参数
		String methodName = "transfer";
		List<Type> inputParameters = new ArrayList<>();
		List<TypeReference<?>> outputParameters = new ArrayList<>();
		Address tAddress = new Address(toAddress);
		Uint256 tokenValue = new Uint256(BigDecimal.valueOf(amount).multiply(BigDecimal.TEN.pow(decimals)).toBigInteger());
		inputParameters.add(tAddress);
		inputParameters.add(tokenValue);
		TypeReference<Bool> typeReference = new TypeReference<Bool>() {
		};
		outputParameters.add(typeReference);
		Function function = new Function(methodName, inputParameters, outputParameters);
		String data = FunctionEncoder.encode(function);

		byte chainId = ChainId.NONE;
		String signedData;
		try {
			signedData = ColdWallet.signTransaction(nonce, gasPrice, gasLimit, contractAddress, value, data, chainId, privateKey);
			if (signedData != null) {
				EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedData).send();
				System.out.println(ethSendTransaction.getTransactionHash());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 创建钱包
	 *
	 * @param password 密码
	 */
	public static void createWallet(String password) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CipherException, JsonProcessingException {
		WalletFile walletFile;
		ECKeyPair ecKeyPair = Keys.createEcKeyPair();
		walletFile = Wallet.createStandard(password, ecKeyPair);
		System.out.println("address " + walletFile.getAddress());
		ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
		String jsonStr = objectMapper.writeValueAsString(walletFile);
		System.out.println("keystore json file " + jsonStr);
	}

	/**
	 * 解密keystore 得到私钥
	 *
	 * @param keystore
	 * @param password
	 */
	public static String decryptWallet(String keystore, String password) {
		String privateKey = null;
		ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
		try {
			WalletFile walletFile = objectMapper.readValue(keystore, WalletFile.class);
			ECKeyPair ecKeyPair = null;
			ecKeyPair = Wallet.decrypt(password, walletFile);
			privateKey = ecKeyPair.getPrivateKey().toString(16);
			System.out.println(privateKey);
		} catch (CipherException e) {
			if ("Invalid password provided".equals(e.getMessage())) {
				System.out.println("密码错误");
			}
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return privateKey;
	}

	/**
	 * 签名交易
	 */
	public static String signTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to,
										 BigInteger value, String data, byte chainId, String privateKey) throws IOException {
		byte[] signedMessage;
		RawTransaction rawTransaction = RawTransaction.createTransaction(
				nonce,
				gasPrice,
				gasLimit,
				to,
				value,
				data);

		if (privateKey.startsWith("0x")) {
			privateKey = privateKey.substring(2);
		}
		ECKeyPair ecKeyPair = ECKeyPair.create(new BigInteger(privateKey, 16));
		Credentials credentials = Credentials.create(ecKeyPair);

		if (chainId > ChainId.NONE) {
			signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
		} else {
			signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
		}

		String hexValue = Numeric.toHexString(signedMessage);
		return hexValue;
	}

}
