package com.ethjava;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Created by Administrator on 2018/4/27.
 */
public class BlockChainJdbcTemplate extends JdbcTemplate {

    private BlockChainJdbcTemplate(DriverManagerDataSource dataSource){
        super(dataSource);
    }


    public static BlockChainJdbcTemplate newInstance(){

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://192.168.22.48:3306/blockchain?auseUnicode=true&characterEncoding=utf8&autoReconnect=true");
        dataSource.setUsername("root");
        dataSource.setPassword("gaoxun");

        return new BlockChainJdbcTemplate(dataSource);
    }



    @Nullable
    public <T> T queryForObject(String sql, @Nullable Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
        List<T> results = (List)this.query((String)sql, (Object[])args, (ResultSetExtractor)(new RowMapperResultSetExtractor(rowMapper, 1)));
        if(results == null || results.size() < 1){
            return null;
        }
        return DataAccessUtils.nullableSingleResult(results);
    }


}
