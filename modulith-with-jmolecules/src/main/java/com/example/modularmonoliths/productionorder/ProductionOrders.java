package com.example.modularmonoliths.productionorder;

import java.util.Collection;
import java.util.Optional;

import org.jmolecules.ddd.types.Repository;
import org.jmolecules.ddd.integration.AssociationResolver;

import com.example.modularmonoliths.productionorder.ProductionOrder.ProductionOrderIdentifier;

public interface ProductionOrders extends Repository<ProductionOrder, ProductionOrderIdentifier>, AssociationResolver<ProductionOrder, ProductionOrderIdentifier> {

    Collection<ProductionOrder> findAll();

    ProductionOrder save(ProductionOrder productionOrder);

    Optional<ProductionOrder> findById(ProductionOrderIdentifier id);
    
    void deleteAll();
    
    int count();
		
}

