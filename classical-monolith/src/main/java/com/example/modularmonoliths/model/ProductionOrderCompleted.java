package com.example.modularmonoliths.model;

import java.util.UUID;

import com.example.modularmonoliths.common.event.DomainEvent;
import com.example.modularmonoliths.common.type.Quantity;
import com.example.modularmonoliths.model.Product.ProductIdentifier;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class ProductionOrderCompleted implements DomainEvent {

    @NonNull
    UUID productionOrderId;

    @NonNull
    ProductIdentifier productIdentifier;

    @NonNull
    Quantity producedQuantity;

}
