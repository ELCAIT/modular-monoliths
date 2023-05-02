package com.example.modularmonoliths.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.example.modularmonoliths.common.exception.EntityNotFoundException;
import com.example.modularmonoliths.common.type.Source;
import com.example.modularmonoliths.model.ProductInventory;
import com.example.modularmonoliths.model.ProductionOrderCompleted;
import com.example.modularmonoliths.model.Product.ProductIdentifier;
import com.example.modularmonoliths.repository.ProductInventoryRepository;
import com.example.modularmonoliths.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@RequiredArgsConstructor
class ProductInventoryService {

    private final ProductInventoryRepository productInventories;
    private final ProductRepository products;

    @EventListener
    void on(ProductionOrderCompleted event) {
        val inventory = productInventories.findById(event.getProductIdentifier().uuidValue())
                .orElseGet(() -> this.createInventory(event.getProductIdentifier()));
        productInventories.save(inventory.replenish(
                event.getProducedQuantity().intValue(),
                Source.of("ProductionOrder " + event.getProductionOrderId())));
    }

    private ProductInventory createInventory(ProductIdentifier productId) {
        return products.findById(productId.uuidValue())
                .map(ProductInventory::createFor)
                .map(productInventories::save)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + productId + " not found"));
    }

}
