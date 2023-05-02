package com.example.modularmonoliths.masterdata;

import java.util.UUID;

import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.ddd.types.Identifier;
import org.jmolecules.event.types.DomainEvent;
import org.springframework.data.domain.AbstractAggregateRoot;

import com.example.modularmonoliths.masterdata.Product.ProductIdentifier;

import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Product extends AbstractAggregateRoot<Product> implements AggregateRoot<Product, ProductIdentifier> {

    private final ProductIdentifier id;

    @Version
    private int version;

    private String name;

    private ProductState state = ProductState.ACTIVE;
    
    public static Product create(String name) {
        val id = ProductIdentifier.random();
        val result = new Product(id);
        result.name = name;
        result.registerEvent(ProductCreated.of(id));
        return result;
    }

    public Product rename(String newName) {
        registerEvent(ProductNameChanged.builder()
                .productId(id)
                .oldName(name)
                .newName(newName)
                .build());
        name = newName;
        return this;
    }

    public Product discontinue() {
        registerEvent(ProductDiscontinued.of(id));
        state = ProductState.DISCONTINUED;
        return this;
    }

    // --------------------------------------------------------------------------------------------

    public record ProductIdentifier(String stringValue) implements Identifier {
    	
    	public static ProductIdentifier random() {
			return new ProductIdentifier(UUID.randomUUID().toString());
		}
    	    
    }

    @Value
    @Builder
    public static class ProductNameChanged implements DomainEvent {
        @NonNull ProductIdentifier productId;
        @NonNull String oldName;
        @NonNull String newName;
    }

    @Value(staticConstructor = "of")
    public static class ProductCreated implements DomainEvent {
        @NonNull ProductIdentifier productId;
    }

    @Value(staticConstructor = "of")
    public static class ProductDiscontinued implements DomainEvent {
        @NonNull ProductIdentifier productId;
    }

    public enum ProductState {
        ACTIVE,
        DISCONTINUED
    }

}

