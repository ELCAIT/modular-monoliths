package com.example.modularmonoliths.masterdata;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.AbstractAggregateRoot;

import com.example.modularmonoliths.common.event.DomainEvent;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Product extends AbstractAggregateRoot<Product> {

    @Id
    private final UUID id;

    @Version
    private int version;

    private String name;

    private ProductState state = ProductState.ACTIVE;

    public static Product create(String name) {
        val id = UUID.randomUUID();
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

    @Value
    @Builder
    public static class ProductNameChanged implements DomainEvent {
        @NonNull UUID productId;
        @NonNull String oldName;
        @NonNull String newName;
    }

    @Value(staticConstructor = "of")
    public static class ProductCreated implements DomainEvent {
        @NonNull UUID productId;
    }

    @Value(staticConstructor = "of")
    public static class ProductDiscontinued implements DomainEvent {
        @NonNull UUID productId;
    }

    public enum ProductState {
        ACTIVE,
        DISCONTINUED
    }

}

