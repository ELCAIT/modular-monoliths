package com.example.modularmonoliths.productinventory;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface ProductInventories extends CrudRepository<ProductInventory, UUID> {

}
