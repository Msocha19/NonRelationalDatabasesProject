package model.mappers;

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.DaoKeyspace;
import com.datastax.oss.driver.api.mapper.annotations.DaoTable;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;
import model.dao.LoanDao;

@Mapper
public interface LoanMapper {

    @DaoFactory
    LoanDao loanDao(@DaoKeyspace String keyspace, @DaoTable String table);

    @DaoFactory
    LoanDao loanDao();
}
