package com.example.modularmonoliths.productionorder;

import org.springframework.stereotype.Service;

import com.example.modularmonoliths.common.type.Quantity;
import com.example.modularmonoliths.masterdata.Product;

import lombok.AllArgsConstructor;
import lombok.val;

@Service
@AllArgsConstructor
public class ProductionOrderService {

    private final ProductionOrders productionOrders;

    public ProductionOrder createOrder(Product product, String productName, Quantity quantityToProduce) {
        val result = ProductionOrder.create(productName, product, quantityToProduce);
        return productionOrders.save(result);
    }

}
