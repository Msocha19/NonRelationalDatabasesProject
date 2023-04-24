package model.mappers;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.DaoTable;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import model.dao.AccountDao;

@Mapper
public interface AccountMapper {

    @DaoFactory
    AccountDao accountDao(@DaoKeyspace String keyspace, @DaoTable String table);

    @DaoFactory
    AccountDao accountDao();
}
