package com.example.modularmonoliths.masterdata;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface Products extends CrudRepository<Product, UUID> {

}
