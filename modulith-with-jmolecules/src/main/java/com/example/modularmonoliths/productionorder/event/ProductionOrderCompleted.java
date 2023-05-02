package com.example.modularmonoliths.productionorder.event;

import org.jmolecules.event.types.DomainEvent;

import com.example.modularmonoliths.common.type.Quantity;
import com.example.modularmonoliths.masterdata.Product.ProductIdentifier;
import com.example.modularmonoliths.productionorder.ProductionOrder.ProductionOrderIdentifier;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class ProductionOrderCompleted implements DomainEvent {

    @NonNull
    ProductionOrderIdentifier productionOrderId;

    @NonNull
    ProductIdentifier productId;

    @NonNull
    Quantity producedQuantity;

}
