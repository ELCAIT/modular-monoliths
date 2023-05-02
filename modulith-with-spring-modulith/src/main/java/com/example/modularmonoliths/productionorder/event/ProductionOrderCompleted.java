package com.example.modularmonoliths.productionorder.event;

import java.util.UUID;

import com.example.modularmonoliths.common.event.DomainEvent;
import com.example.modularmonoliths.common.type.Quantity;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class ProductionOrderCompleted implements DomainEvent {

    @NonNull
    UUID productionOrderId;

    @NonNull
    UUID productId;

    @NonNull
    Quantity producedQuantity;

}
