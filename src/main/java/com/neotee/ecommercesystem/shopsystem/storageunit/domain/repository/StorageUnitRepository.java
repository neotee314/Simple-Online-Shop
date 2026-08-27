package com.neotee.ecommercesystem.shopsystem.storageunit.domain.repository;

import com.neotee.ecommercesystem.domainprimitives.StorageUnitId;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.model.StorageUnit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageUnitRepository extends CrudRepository<StorageUnit, StorageUnitId> {
    @Override
    List<StorageUnit> findAll();
}
