package com.example.modularmonoliths.productinventory;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.example.modularmonoliths.common.exception.EntityNotFoundException;
import com.example.modularmonoliths.common.type.Source;
import com.example.modularmonoliths.masterdata.Product.ProductIdentifier;
import com.example.modularmonoliths.masterdata.Products;
import com.example.modularmonoliths.productionorder.event.ProductionOrderCompleted;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@RequiredArgsConstructor
class ProductInventoryService {

    private final ProductInventories productInventories;
    private final Products products;

    @EventListener
    void on(ProductionOrderCompleted event) {
        val inventory = productInventories.findById(event.getProductId())
                .orElseGet(() -> this.createInventory(event.getProductId()));
        productInventories.save(inventory.replenish(
                event.getProducedQuantity().intValue(),
                Source.of("ProductionOrder " + event.getProductionOrderId())));
    }

    private ProductInventory createInventory(ProductIdentifier productId) {
        return products.findById(productId)
                .map(ProductInventory::createFor)
                .map(productInventories::save)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + productId + " not found"));
    }

}
