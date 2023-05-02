package com.example.modularmonoliths.service;

import org.springframework.stereotype.Service;

import com.example.modularmonoliths.common.exception.EntityNotFoundException;
import com.example.modularmonoliths.common.type.Quantity;
import com.example.modularmonoliths.model.ProductionOrder;
import com.example.modularmonoliths.model.Product.ProductIdentifier;
import com.example.modularmonoliths.repository.ProductRepository;
import com.example.modularmonoliths.repository.ProductionOrderRepository;

import lombok.AllArgsConstructor;
import lombok.val;

@Service
@AllArgsConstructor
public class ProductionOrderService {

    private final ProductRepository products;
    private final ProductionOrderRepository productionOrders;

    public ProductionOrder createOrder(String name, ProductIdentifier productId, Quantity quantityToProduce) {
        val product = products.findById(productId.uuidValue())
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + productId + " not found"));
        val result = ProductionOrder.create(name, product, quantityToProduce);
        return productionOrders.save(result);
    }

}
