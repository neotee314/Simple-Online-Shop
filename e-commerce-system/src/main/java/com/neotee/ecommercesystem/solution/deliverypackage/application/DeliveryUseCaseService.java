package com.neotee.ecommercesystem.solution.deliverypackage.application;

import com.neotee.ecommercesystem.ShopException;
import com.neotee.ecommercesystem.solution.deliverypackage.domain.DeliveryPackage;
import com.neotee.ecommercesystem.solution.deliverypackage.domain.DeliveryPackagePart;
import com.neotee.ecommercesystem.solution.deliverypackage.domain.DeliveryPackageRepository;
import com.neotee.ecommercesystem.solution.order.application.OrderService;
import com.neotee.ecommercesystem.solution.storageunit.application.StorageUnitService;
import com.neotee.ecommercesystem.solution.storageunit.domain.StorageUnit;
import com.neotee.ecommercesystem.usecases.DeliveryPackageUseCases;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DeliveryUseCaseService implements DeliveryPackageUseCases {

    private final DeliveryPackageRepository deliveryPackageRepository;
    private final StorageUnitService storageUnitService;


    @Override
    @Transactional
    public List<UUID> getContributingStorageUnitsForOrder(UUID orderId) {
        // گرفتن همه DeliveryPackageهایی که مربوط به orderId هستند
        List<DeliveryPackage> deliveryPackages = deliveryPackageRepository.findByOrderId(orderId);
        if (deliveryPackages.isEmpty()) throw new ShopException("Order does not exist");

        // ایجاد لیست برای ذخیره ID انبارهایی که در این سفارش مشارکت دارند
        List<UUID> contributingStorageUnits = new ArrayList<>();

        // برای هر DeliveryPackage که پیدا شده، ID انبار را اضافه می‌کنیم
        for (DeliveryPackage deliveryPackage : deliveryPackages) {
            UUID storageUnitId = deliveryPackage.getStorageUnitId();

            // فقط انبارهایی که قبلا افزوده نشده‌اند به لیست اضافه می‌کنیم
            if (!contributingStorageUnits.contains(storageUnitId)) {
                contributingStorageUnits.add(storageUnitId);
            }
        }

        return contributingStorageUnits;
    }


    @Override
    @Transactional
    public Map<UUID, Integer> getDeliveryPackageForOrderAndStorageUnit(UUID orderId, UUID storageUnitId) {
        // گرفتن DeliveryPackage مربوط به orderId و storageUnitId
        List<DeliveryPackage> deliveryPackages = deliveryPackageRepository.findByOrderId(orderId);
        if (deliveryPackages.isEmpty()) throw new ShopException("Order does not exist");

        Map<UUID, Integer> deliveryPackageMap = new HashMap<>();

        // پیدا کردن DeliveryPackageهایی که مربوط به انبار و سفارش داده‌شده هستند
        for (DeliveryPackage deliveryPackage : deliveryPackages) {
            if (deliveryPackage.hasStorage(storageUnitId)) {
                // برای هر بسته تحویل، قسمت‌های آن را به Map اضافه می‌کنیم
                for (DeliveryPackagePart part : deliveryPackage.getParts()) {
                    deliveryPackageMap.put(part.getThingId(), part.getQuantity());
                }
            }
        }

        return deliveryPackageMap;
    }

    @Override
    public void deleteAllDeliveryPackages() {
        deliveryPackageRepository.deleteAll();
    }
}
