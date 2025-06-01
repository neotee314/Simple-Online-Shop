package com.neotee.ecommercesystem.solution.storageunit.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StorageUnitRepository extends CrudRepository<StorageUnit, StorageUnitId> {
    @Override
    List<StorageUnit> findAll();
}
