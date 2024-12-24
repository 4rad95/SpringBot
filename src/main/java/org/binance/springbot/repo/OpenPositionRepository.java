package org.binance.springbot.repo;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.binance.springbot.entity.OpenPosition;
import org.binance.springbot.service.OpenPositionService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OpenPositionRepository extends JpaRepository<OpenPosition,Long> {

    @Query("SELECT count(*) as count FROM  OpenPosition ")
   Integer getCount();

}

