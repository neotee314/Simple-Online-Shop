package com.neotee.ecommercesystem.helper;

import com.neotee.ecommercesystem.usecases.StorageUnitUseCases;
import com.neotee.ecommercesystem.usecases.ProductCatalogUseCases;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.HomeAddressType;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.MoneyType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@SuppressWarnings("PMD")
@Component
public class ThingAndStockMasterDataInitializer {

    private ProductCatalogUseCases productCatalogUseCases;
    private StorageUnitUseCases storageUnitUseCases;
    private static Random random = new Random();

    public static final String EUR = "EUR";

    @Autowired
    public ThingAndStockMasterDataInitializer(ProductCatalogUseCases productCatalogUseCases,
                                              StorageUnitUseCases storageUnitUseCases) {
        this.productCatalogUseCases = productCatalogUseCases;
        this.storageUnitUseCases = storageUnitUseCases;
    }

    public static final int THING_NUMOF = 15;
    public static final Object[][] THING_DATA = new Object[][]{
            {UUID.fromString("11111111-1111-1111-1111-111111111111"), "0-TCD-34 v2.1", "Universelles Verbindungsstück für den einfachen Hausgebrauch bei der Schnellmontage",
                    1.5f, FactoryMethodInvoker.instantiateMoney(5.0f, EUR),
                    FactoryMethodInvoker.instantiateMoney(10.0f, EUR), "0"},
            {UUID.fromString("22222222-2222-2222-2222-222222222222"), "1-EFG-56", "Hochleistungsfähiger Kondensator für elektronische Schaltungen",
                    0.3f, FactoryMethodInvoker.instantiateMoney(2.5f, EUR),
                    FactoryMethodInvoker.instantiateMoney(4.0f, EUR), "0"},
            {UUID.fromString("33333333-3333-3333-3333-333333333333"), "2-MNP-89ff", "Langlebiger und robuster Motor für industrielle Anwendungen",
                    7.2f, FactoryMethodInvoker.instantiateMoney(50.0f, EUR),
                    FactoryMethodInvoker.instantiateMoney(80.0f, EUR), "0"},
            {UUID.fromString("44444444-4444-4444-4444-444444444444"), "3-Gh-25", "Kompakter und leichter Akku für mobile Geräte",
                    null, FactoryMethodInvoker.instantiateMoney(6.0f, EUR),
                    FactoryMethodInvoker.instantiateMoney(8.0f, EUR), "0"},
            {UUID.fromString("55555555-5555-5555-5555-555555555555"), "4-MultiBeast2", "Vielseitiger Adapter für verschiedene Steckertypen",
                    null, FactoryMethodInvoker.instantiateMoney(0.6f, EUR),
                    FactoryMethodInvoker.instantiateMoney(1.0f, EUR), "0"},
            {UUID.fromString("66666666-6666-6666-6666-666666666666"), "5-ABC-99 v4.2", "Leistungsstarker Prozessor für Computer und Server",
                    1.0f, FactoryMethodInvoker.instantiateMoney(150.0f, EUR),
                    FactoryMethodInvoker.instantiateMoney(250.0f, EUR), "0"},
            {UUID.fromString("77777777-7777-7777-7777-777777777777"), "6-Stuko22", "Ersatzteil Spitze für Präzisionswerkzeug zum Löten und Schrauben",
                    null, FactoryMethodInvoker.instantiateMoney(0.3f, EUR),
                    FactoryMethodInvoker.instantiateMoney(0.5f, EUR), "0"},
            {UUID.fromString("88888888-8888-8888-8888-888888888888"), "7-Btt2-Ah67", "Kraftstoffeffiziente und umweltfreundliche Hochleistungsbatterie",
                    6.0f, FactoryMethodInvoker.instantiateMoney(80.0f, EUR),
                    FactoryMethodInvoker.instantiateMoney(120.0f, EUR), "123"},
            {UUID.fromString("99999999-9999-9999-9999-999999999999"), "8-JKL-67", "Wasserdichtes Gehäuse",
                    3.0f, FactoryMethodInvoker.instantiateMoney(1.0f, EUR),
                    FactoryMethodInvoker.instantiateMoney(1.2f, EUR), "467"},
            {UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"), "9-MNO-55-33", "Modulares Netzteil für flexible Stromversorgung",
                    5.5f, FactoryMethodInvoker.instantiateMoney(25.0f, EUR),
                    FactoryMethodInvoker.instantiateMoney(45.0f, EUR), "578"},
            {UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"), "10-PQR-80", "Effizienter Kühler für verbesserte Wärmeableitung",
                    4.0f, FactoryMethodInvoker.instantiateMoney(20.0f, EUR),
                    FactoryMethodInvoker.instantiateMoney(35.0f, EUR), "567"},
            {UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"), "11-STU-11 Ld", "Hochwertiger Grafikchip für leistungsstarke PCs",
                    null, FactoryMethodInvoker.instantiateMoney(200.0f, EUR),
                    FactoryMethodInvoker.instantiateMoney(350.0f, EUR), "478"},
            {UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"), "12-VWX-90 FastWupps", "Schnellladegerät für eine Vielzahl von Geräten",
                    null, FactoryMethodInvoker.instantiateMoney(15.0f, EUR),
                    FactoryMethodInvoker.instantiateMoney(25.0f, EUR), "5"},
            {UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee"), "13-YZZ-22 v1.8", "Leichter und stabiler Rahmen aus Aluminium",
                    3.5f, FactoryMethodInvoker.instantiateMoney(60.0f, EUR),
                    FactoryMethodInvoker.instantiateMoney(100.0f, EUR), "78"},
            {UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff"), "14-Nosedive", "Klammer zum Verschließen der Nase beim Tauchen",
                    5.0f, FactoryMethodInvoker.instantiateMoney(2.0f, EUR),
                    FactoryMethodInvoker.instantiateMoney(5.0f, EUR), "457"}
    };

    public void addAllThings() {
        System.out.println("Adding all things to the catalog.");
        for (Object[] thingData : THING_DATA) {
            addThingDataToCatalog(thingData);
        }
        System.out.println("All things are added to catalog.");
    }

    public void addThingDataToCatalog(Object[] thingData) {
        System.out.println("... adding " + thingData[1]);
        productCatalogUseCases.addProductToCatalog((UUID) thingData[0], (String) thingData[1], (String) thingData[2],
                (Float) thingData[3], (MoneyType) thingData[4], (MoneyType) thingData[5]);
    }

    public enum InvalidReason {
        NULL, EMPTY;

        public Object getInvalidValue(Object originalValue) {
            switch (this) {
                case NULL:
                    return null;
                case EMPTY:
                    return "";
                default:
                    return null;
            }
        }
    }

    public Object[] getThingDataInvalidAtIndex(int index, InvalidReason reason) {
        Object[] thingData = THING_DATA[1];
        Object[] thingDataInvalid = new Object[thingData.length];
        System.arraycopy(thingData, 0, thingDataInvalid, 0, thingData.length);
        thingDataInvalid[index] = thingData[index].getClass().cast(
                reason.getInvalidValue(thingData[index]));
        return thingDataInvalid;
    }

    // Storage Units
    public final static int STORAGE_UNIT_NUMOF = 10;
    public final static HomeAddressType[] STORAGE_UNIT_ADDRESS = new HomeAddressType[]{
            FactoryMethodInvoker.instantiateHomeAddress("Stapelallee 0", "Potsdam", FactoryMethodInvoker.instantiateZipCode("14476")),
            FactoryMethodInvoker.instantiateHomeAddress("Lagerhausstr. 1", "Viertelstadt", FactoryMethodInvoker.instantiateZipCode("02345")),
            FactoryMethodInvoker.instantiateHomeAddress("Speicherplatz 2", "Viertelstadt", FactoryMethodInvoker.instantiateZipCode("02313")),
            FactoryMethodInvoker.instantiateHomeAddress("Ablageweg 3", "Reichswürgen", FactoryMethodInvoker.instantiateZipCode("44923")),
            FactoryMethodInvoker.instantiateHomeAddress("Paketstellenallee 4", "Düsseldorf", FactoryMethodInvoker.instantiateZipCode("40588")),
            FactoryMethodInvoker.instantiateHomeAddress("Kaputte-Sachen-Straße 5", "Düren", FactoryMethodInvoker.instantiateZipCode("52355")),
            FactoryMethodInvoker.instantiateHomeAddress("Aufbewahrungsweg 6", "Viernheim", FactoryMethodInvoker.instantiateZipCode("68519")),
            FactoryMethodInvoker.instantiateHomeAddress("Paketallee 7", "Baden-Baden", FactoryMethodInvoker.instantiateZipCode("76532")),
            FactoryMethodInvoker.instantiateHomeAddress("Sendenstr. 8", "Senden", FactoryMethodInvoker.instantiateZipCode("89250")),
            FactoryMethodInvoker.instantiateHomeAddress("Schickweg 9", "Hohenroth", FactoryMethodInvoker.instantiateZipCode("97618"))
    };
    public final static UUID[] STORAGE_UNIT_ID = new UUID[STORAGE_UNIT_NUMOF];

    public void addAllStorageUnits() {
        System.out.println("Adding all storage units.");
        for (int i = 0; i < STORAGE_UNIT_NUMOF; i++) {
            STORAGE_UNIT_ID[i] = storageUnitUseCases.addNewStorageUnit(
                    STORAGE_UNIT_ADDRESS[i], STORAGE_UNIT_ADDRESS[i].getZipCode().toString());
        }
        System.out.println("All storage units are added.");
    }

    // THING_STOCK
    public static final Map<UUID, Integer[]> THING_STOCK = new HashMap<>();

    static {
        THING_STOCK.put((UUID) THING_DATA[0][0], getStockDistribution(0, (String) THING_DATA[0][6]));
        THING_STOCK.put((UUID) THING_DATA[1][0], getStockDistribution(10, (String) THING_DATA[1][6]));
        THING_STOCK.put((UUID) THING_DATA[2][0], getStockDistribution(20, (String) THING_DATA[2][6]));
        THING_STOCK.put((UUID) THING_DATA[3][0], getStockDistribution(30, (String) THING_DATA[3][6]));

        for (int i = 4; i < THING_NUMOF; i++) {
            Integer totalNumber = random.nextInt(100) + 30;
            Integer[] stockInStorageUnits = getStockDistribution(totalNumber, (String) THING_DATA[i][6]);
            THING_STOCK.put((UUID) THING_DATA[i][0], stockInStorageUnits);
        }
    }

    private static Integer[] getStockDistribution(Integer totalQuantity, String zeroToNine) {
        Integer[] stockInStorageUnits = new Integer[STORAGE_UNIT_NUMOF];
        for (int i = 0; i < STORAGE_UNIT_NUMOF; i++) stockInStorageUnits[i] = 0;
        TreeSet<Integer> storageUnitIndices = getStorageUnitIndices(zeroToNine);
        int numOfIndices = storageUnitIndices.size();
        int currentIndexNumber = 0;
        int currentQuantity = totalQuantity;
        for (Integer storageUnitIndex : storageUnitIndices) {
            currentIndexNumber++;
            if (currentIndexNumber < numOfIndices) {
                stockInStorageUnits[storageUnitIndex] = 3;
                currentQuantity -= 3;
            } else {
                stockInStorageUnits[storageUnitIndex] = currentQuantity;
            }
        }
        return stockInStorageUnits;
    }

    private static TreeSet<Integer> getStorageUnitIndices(String zeroToNine) {
        TreeSet<Integer> storageUnitIndices = new TreeSet<>();
        for (int i = 0; i < zeroToNine.length(); i++) {
            storageUnitIndices.add(Integer.parseInt(zeroToNine.substring(i, i + 1)));
        }
        return storageUnitIndices;
    }

    public void addAllStock() {
        System.out.println("Adding all stocks to the storage units.");
        for (Object[] thingData : THING_DATA) {
            Integer[] stockInStorageUnits = THING_STOCK.get(thingData[0]);
            for (int iStorageUnit = 0; iStorageUnit < STORAGE_UNIT_NUMOF; iStorageUnit++) {
                if (stockInStorageUnits[iStorageUnit] > 0)
                    storageUnitUseCases.addToStock(
                            STORAGE_UNIT_ID[iStorageUnit], (UUID) thingData[0],
                            stockInStorageUnits[iStorageUnit]);
            }
        }
    }

    public Integer findStorageUnitIndex(UUID storageUnitId) {
        for (int i = 0; i < STORAGE_UNIT_NUMOF; i++) {
            if (STORAGE_UNIT_ID[i].equals(storageUnitId)) {
                return i;
            }
        }
        return null;
    }

    public Float getTotalSalesPrice(Map<UUID, Integer> quantityThingMap) {
        Float totalPrice = 0.0f;
        for (UUID thingId : quantityThingMap.keySet()) {
            Integer quantity = quantityThingMap.get(thingId);
            int thingIndex = -1;
            for (int i = 0; i < THING_NUMOF; i++) {
                if (THING_DATA[i][0].equals(thingId)) {
                    thingIndex = i;
                    break;
                }
            }
            totalPrice += ((MoneyType) THING_DATA[thingIndex][5]).getAmount() * quantity;
        }
        return totalPrice;
    }
}