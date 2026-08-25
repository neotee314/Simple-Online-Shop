package com.neotee.ecommercesystem.shopsystem.storageunit.domain;

import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageUnitRepository extends CrudRepository<StorageUnit, StorageUnitId> {
    @Override
    List<StorageUnit> findAll();
}
