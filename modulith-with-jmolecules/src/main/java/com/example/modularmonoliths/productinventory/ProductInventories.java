package com.example.modularmonoliths.productinventory;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.example.modularmonoliths.masterdata.Product.ProductIdentifier;

@RepositoryRestResource(exported = false)
public interface ProductInventories extends CrudRepository<ProductInventory, ProductIdentifier> {

}
