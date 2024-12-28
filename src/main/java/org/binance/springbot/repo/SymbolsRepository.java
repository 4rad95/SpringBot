package org.binance.springbot.repo;


import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.binance.springbot.entity.Symbols;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SymbolsRepository extends JpaRepository<Symbols,Long> {
    Symbols findBySymbols(String symbols);

    void deleteBySymbols(String symbols);
}
