package com.neotee.ecommercesystem.shopsystem.storageunit.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockLevelRepository extends CrudRepository<StockLevel, StockLevelId> {

    @Override
    List<StockLevel> findAll();
}
