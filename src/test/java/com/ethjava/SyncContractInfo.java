package com.ethjava;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ethjava.model.ContractDTO;
import com.ethjava.utils.Environment;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by Administrator on 2018/4/26.
 */
public class SyncContractInfo{

    static BlockChainJdbcTemplate jt = BlockChainJdbcTemplate.newInstance();

    public static void main(String[] args) throws IOException {

        syncContractInfoByHttp();
        
        syncContractInfoByChain();
    }

    private static void syncContractInfoByChain() throws IOException {

        Web3j web3j = Web3j.build(new HttpService(Environment.RPC_URL));

        EthBlockNumber number = web3j.ethBlockNumber().send();

        for (BigInteger i = BigInteger.ZERO; i.compareTo(number.getBlockNumber()) < 1; i = i.add(BigInteger.ONE)) {
            List<EthBlock.TransactionResult> transactions = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(i), true).send().getBlock().getTransactions();
            transactions.forEach(tx -> {
                EthBlock.TransactionObject tr = (EthBlock.TransactionObject) tx.get();

                String input = tr.getInput();
                if(StringUtils.isNotEmpty(input) && input.startsWith("0xa9059cbb")){
                    String contractCode = tr.getTo();

                    boolean existInDB = isExistInDB(contractCode);
                    ContractDTO contractDTO = new ContractDTO();

                    if(!existInDB){
                        contractDTO.setAddress(contractCode);
                        insert(contractDTO);
                    }else{
                        //修改
                    }
                }
            });
        }
    }

    private static void syncContractInfoByHttp() {

        try (CloseableHttpClient httpCilent = HttpClients.createDefault()) {

            HttpGet httpGet = new HttpGet("https://api.ethplorer.io/getTopTokens?apiKey=freekey&criteria=cap");
            HttpResponse httpResponse = httpCilent.execute(httpGet);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {

                List<ContractDTO> contractDTOS = parseContracts(httpResponse);
                for (ContractDTO contract : contractDTOS) {
                    boolean isExistInDB = isExistInDB(contract.getAddress());
                    if(!isExistInDB) {
                        insert(contract);
                    }else{
                        //修改
                    }
                }

            } else {
                System.out.println("请求异常 statusCode:"+httpResponse.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<ContractDTO> parseContracts(HttpResponse httpResponse) throws IOException {

        String srtResult = EntityUtils.toString(httpResponse.getEntity());//获得返回的结果
        JSONObject js = (JSONObject) JSONObject.parse(srtResult);

        JSONArray jsArr = (JSONArray) js.get("tokens");
        List<ContractDTO> contractDTOS = jsArr.toJavaList(ContractDTO.class);
        return contractDTOS;
    }

    private static void insert(ContractDTO contract) {
        jt.update("insert  into public_contract_info(address,name,decimals,symbol,totalSupply) VALUE (?,?,?,?,?) ",
                contract.getAddress(),
                contract.getName(),
                contract.getDecimals(),
                contract.getSymbol(),
                contract.getTotalSupply());
    }

    private static boolean isExistInDB(String address) {

        String sql = "select address,name,decimals,symbol,totalSupply from public_contract_info where address = ?";
        ContractDTO contractDTO = jt.queryForObject(sql, new String[]{address}, new BeanPropertyRowMapper<ContractDTO>(ContractDTO.class));
        return contractDTO != null;

    }


}
