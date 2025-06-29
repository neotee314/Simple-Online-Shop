package com.neotee.ecommercesystem.shopsystem.storageunit.application.service;

import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.shopsystem.storageunit.application.dto.AvailableStockDTO;
import com.neotee.ecommercesystem.shopsystem.storageunit.application.dto.StockResponseDTO;
import com.neotee.ecommercesystem.shopsystem.storageunit.application.dto.StorageUnitRequestDTO;
import com.neotee.ecommercesystem.shopsystem.storageunit.application.dto.StorageUnitResponseDTO;
import com.neotee.ecommercesystem.shopsystem.storageunit.application.mapper.StorageUnitMapper;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnitId;
import com.neotee.ecommercesystem.shopsystem.storageunit.domain.StorageUnitRepository;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.HomeAddressType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageUnitApplicationService {

    private final StorageUnitUseCasesService storageUnitUseCasesService;
    private final StorageUnitMapper storageUnitMapper;
    private final StorageUnitRepository storageUnitRepository;

    public StorageUnitResponseDTO getStorageUnitById(UUID id) {
        return storageUnitRepository.findById(new StorageUnitId(id))
                .map(storageUnitMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Storage unit not found"));
    }

    public List<StorageUnitResponseDTO> getAllStorageUnits() {
        return storageUnitRepository.findAll().stream()
                .map(storageUnitMapper::toDto)
                .collect(Collectors.toList());
    }

    public void addNewStorageUnit(StorageUnitRequestDTO dto) {
        storageUnitUseCasesService.addNewStorageUnit(dto.getAddress(),dto.getName());
    }

    public void deleteAllStorageUnits() {
        storageUnitUseCasesService.deleteAllStorageUnits();
    }

    public void addToStock(StockResponseDTO dto) {
        storageUnitUseCasesService.addToStock(
                dto.getStorageUnitId(),
                dto.getThingId(),
                dto.getQuantity()
        );
    }

    public void removeFromStock(StockResponseDTO dto) {
        storageUnitUseCasesService.removeFromStock(
                dto.getStorageUnitId(),
                dto.getThingId(),
                dto.getQuantity()
        );
    }

    public void changeStockTo(StockResponseDTO dto) {
        storageUnitUseCasesService.changeStockTo(
                dto.getStorageUnitId(),
                dto.getThingId(),
                dto.getQuantity()
        );
    }

    public int getAvailableStock(UUID id) {
        return storageUnitUseCasesService.getAvailableStock(id);
    }

    public int getAvailableStockInStorageUnit(AvailableStockDTO availableStockDTO) {
        return storageUnitUseCasesService.getAvailableStock(
                availableStockDTO.getStorageUnitId(),
                availableStockDTO.getThingId()
        );
    }
}
