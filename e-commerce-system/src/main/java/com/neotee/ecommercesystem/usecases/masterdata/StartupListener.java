package com.neotee.ecommercesystem.usecases.masterdata;

import com.neotee.ecommercesystem.usecases.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;


@Slf4j
@Profile("!test")
public class StartupListener implements ApplicationListener<ContextRefreshedEvent>  {
    private ThingAndStockMasterDataInitializer thingAndStockMasterDataInitializer;
    private ClientMasterDataInitializer clientMasterDataInitializer;

    private ClientRegistrationUseCases clientRegistrationUseCases;
    private ProductCatalogUseCases productCatalogUseCases;
    private StorageUnitUseCases storageUnitUseCases;
    private Purgatory purgatory;


    public StartupListener( ClientRegistrationUseCases clientRegistrationUseCases,
                            ProductCatalogUseCases productCatalogUseCases,
                            StorageUnitUseCases storageUnitUseCases,
                            Purgatory purgatory ) {
        this.clientRegistrationUseCases = clientRegistrationUseCases;
        this.productCatalogUseCases = productCatalogUseCases;
        this.storageUnitUseCases = storageUnitUseCases;
        this.purgatory = purgatory;
        thingAndStockMasterDataInitializer = new ThingAndStockMasterDataInitializer(
                productCatalogUseCases, storageUnitUseCases );
        clientMasterDataInitializer = new ClientMasterDataInitializer( clientRegistrationUseCases );
    }

    @Override
    public void onApplicationEvent( ContextRefreshedEvent contextRefreshedEvent ) {
        log.info( "StartupListener initializing master data..." );
        purgatory.deleteEverything();
        clientMasterDataInitializer = new ClientMasterDataInitializer( clientRegistrationUseCases );
        clientMasterDataInitializer.registerAllClients();

        thingAndStockMasterDataInitializer = new ThingAndStockMasterDataInitializer(
                productCatalogUseCases, storageUnitUseCases );
        thingAndStockMasterDataInitializer.addAllThings();
        thingAndStockMasterDataInitializer.addAllStorageUnits();
        thingAndStockMasterDataInitializer.addAllStock();
    }
}
