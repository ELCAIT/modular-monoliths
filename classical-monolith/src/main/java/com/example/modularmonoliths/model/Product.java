package com.example.modularmonoliths.model;

import java.util.UUID;

import org.springframework.data.annotation.Version;
import org.springframework.data.domain.AbstractAggregateRoot;

import com.example.modularmonoliths.common.event.DomainEvent;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import lombok.val;
import lombok.experimental.Accessors;

@Entity
@Table(name="product")
@EqualsAndHashCode(of = "id", callSuper = false)
@Getter
public class Product extends AbstractAggregateRoot<Product> {

    @Id
    private UUID id;

    @Version
    @Column
    private int version;

    @Column
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private ProductState state = ProductState.ACTIVE;

    public ProductIdentifier getId() {
        return ProductIdentifier.of(id);
    }

    public static Product create(String name) {
        val id = UUID.randomUUID();
        val result = new Product();
        result.id = id;
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

    @Value(staticConstructor = "of")
    @Accessors(fluent = true)
    public static class ProductIdentifier {

    	@NonNull UUID uuidValue;

        @JsonCreator(mode = Mode.DELEGATING)
        ProductIdentifier(UUID uuidValue) {
            this.uuidValue = uuidValue;
        }

        @Override
        public String toString() {
            return uuidValue().toString();
        }
    }

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

