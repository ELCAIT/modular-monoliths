package com.example.modularmonoliths.productionorder;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface ProductionOrders extends CrudRepository<ProductionOrder, UUID> {

}
