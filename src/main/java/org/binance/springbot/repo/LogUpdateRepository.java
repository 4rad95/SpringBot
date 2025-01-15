package org.binance.springbot.repo;


import org.binance.springbot.dto.LogUpdateDto;
import org.binance.springbot.entity.LogUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogUpdateRepository extends JpaRepository<LogUpdate,Long> {

    @Query(value = "SELECT  *  FROM  LOGUPDATE  ORDER BY ID DESC  LIMIT 50",  nativeQuery = true
    )
    List<LogUpdate> getLogUpdate50();
}
