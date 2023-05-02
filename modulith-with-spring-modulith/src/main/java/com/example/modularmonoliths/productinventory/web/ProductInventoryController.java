package com.example.modularmonoliths.productinventory.web;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.modularmonoliths.productinventory.ProductInventories;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productInventories")
@RequiredArgsConstructor
class ProductInventoryController {

    static final String RESOURCE_REL = "productInventories";

    private final ProductInventories productInventories;

    @RequestMapping
    ResponseEntity<?> findAll() {
        return ResponseEntity.ok(CollectionModel.wrap(productInventories.findAll()));
    }

}
