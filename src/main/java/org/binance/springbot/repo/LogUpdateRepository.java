package org.binance.springbot.repo;


import org.binance.springbot.entity.LogUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogUpdateRepository extends JpaRepository<LogUpdate,Long> {

}
