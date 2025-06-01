package com.neotee.ecommercesystem.solution.storageunit.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockLevelRepository extends CrudRepository<StockLevel, StockLevelId> {

    List<StockLevel> findAll();
}
