package com.example.modularmonoliths.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.example.modularmonoliths.model.ProductInventory;

@RepositoryRestResource(exported = false)
public interface ProductInventoryRepository extends CrudRepository<ProductInventory, UUID> {

}
