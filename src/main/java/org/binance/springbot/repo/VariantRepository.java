package org.binance.springbot.repo;

import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.binance.springbot.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantRepository extends JpaRepository<Variant,Long> {
}