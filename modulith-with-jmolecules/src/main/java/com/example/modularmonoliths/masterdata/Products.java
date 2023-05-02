package com.example.modularmonoliths.masterdata;

import java.util.Collection;

import org.jmolecules.ddd.types.Repository;
import org.jmolecules.ddd.integration.AssociationResolver;

import com.example.modularmonoliths.masterdata.Product.ProductIdentifier;

public interface Products 
	extends Repository<Product, ProductIdentifier>, 
	AssociationResolver<Product, ProductIdentifier> {

    Collection<Product> findAll();

    Product save(Product Product);

    int count();

}