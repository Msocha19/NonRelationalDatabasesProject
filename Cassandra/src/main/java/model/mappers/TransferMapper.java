package model.mappers;

import com.datastax.oss.driver.api.mapper.annotations.*;
import model.dao.TransferDao;

@Mapper
public interface TransferMapper {

    @DaoFactory
    TransferDao transferDao(@DaoKeyspace String keyspace, @DaoTable String table);

    @DaoFactory
    TransferDao transferDao();
}
