package com.example.modularmonoliths.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Version;
import org.springframework.data.domain.AbstractAggregateRoot;

import com.example.modularmonoliths.common.event.DomainEvent;
import com.example.modularmonoliths.common.type.Principal;
import com.example.modularmonoliths.common.type.Source;
import com.example.modularmonoliths.model.Product.ProductIdentifier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.val;

@Entity
@Table(name="product_inventory")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "productId", callSuper = false)
public class ProductInventory extends AbstractAggregateRoot<ProductInventory> {

    private static final LocalDateTime MIN_DATE = LocalDateTime.of(2000, 1, 1, 0, 0);

    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Version
    @Column(name = "version")
    private Integer version;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "current_quantity")
    private Integer currentQuantity;

    @Column(name = "notification_threshold")
    private Integer notificationThreshold;

    @Column(name = "last_corrected")
    private LocalDateTime lastCorrected;

    @Column(name = "last_replenished")
    private LocalDateTime lastReplenished;
    
    @Column(name = "last_consumed")
    private LocalDateTime lastConsumed;

    public ProductIdentifier getProductId() {
        return ProductIdentifier.of(productId);
    }

    public static ProductInventory createFor(Product product) {
        val result = new ProductInventory();

        result.productId = product.getId().uuidValue();
        result.productName = product.getName();
        result.currentQuantity = 0;
        result.notificationThreshold = 0;

        result.lastCorrected = MIN_DATE;
        result.lastReplenished = MIN_DATE;
        result.lastConsumed = MIN_DATE;

        result.registerEvent(ProductInventoryCreated.forProduct(result.getProductId()));

        return result;
    }

    public ProductInventory setNotificationThreshold(Integer value) {
        if (value < 0) {
            throw new IllegalArgumentException("Notification threshold must not be negative");
        }
        notificationThreshold = value;
        if (currentQuantity > 0) {
            notifyIfBelowThreshold();
        }
        return this;
    }

    public ProductInventory changeProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public ProductInventory replenish(Integer value, Source source) {
        if (value < 0) {
            throw new IllegalArgumentException("Amount to replanish must be positiv, but got " + value);
        }
        if (value == 0) {
            return this;
        }
        currentQuantity = currentQuantity + value;
        lastReplenished = LocalDateTime.now();
        registerEvent(ProductInventoryReplenished.builder()
                .productId(ProductIdentifier.of(productId))
                .replenishedQuantity(value)
                .newQuantity(currentQuantity)
                .source(source)
                .build());

        val now = LocalDateTime.now();
        lastReplenished = now;

        notifyIfBelowThreshold();
        return this;
    }

    public ProductInventory consume(Integer value, Source source) {
        currentQuantity = currentQuantity - value;
        lastConsumed = LocalDateTime.now();
        registerEvent(ProductInventoryConsumed.builder()
                .productId(getProductId())
                .consumedQuantity(value)
                .newQuantity(currentQuantity)
                .source(source)
                .build());
        notifyIfBelowThreshold();
        return this;
    }

    public ProductInventory correct(Integer value, Principal principal) {
        val oldQuantity = currentQuantity;
        currentQuantity = value;
        lastCorrected = LocalDateTime.now();
        registerEvent(ProductInventoryCorrected.builder()
                .productId(getProductId())
                .oldQuantity(oldQuantity)
                .newQuantity(currentQuantity)
                .principal(principal)
                .build());
        notifyIfBelowThreshold();
        return this;
    }


    private void notifyIfBelowThreshold() {
        if (notificationThreshold > 0
                && currentQuantity.compareTo(notificationThreshold) < 0) {
            registerEvent(ProductInventoryThresholdUnderrun.builder()
                    .productId(getProductId())
                    .currentQuantity(currentQuantity)
                    .threshold(notificationThreshold)
                    .build());
        }
        if (currentQuantity <= 0) {
            registerEvent(ProductInventoryEmptied.builder()
                    .productId(getProductId())
                    .currentQuantity(currentQuantity)
                    .build());
        }
    }

    public interface ProductInventoryEvent extends DomainEvent {

        ProductIdentifier getProductId();

    }

    @Value(staticConstructor = "forProduct")
    public static class ProductInventoryCreated implements ProductInventoryEvent {

        @NonNull
        ProductIdentifier productId;

    }

    @Value
    @Builder
    public static class ProductInventoryReplenished implements ProductInventoryEvent {

        @NonNull
        ProductIdentifier productId;

        @NonNull
        Integer replenishedQuantity;

        @NonNull
        Integer newQuantity;

        @NonNull
        @Builder.Default
        Source source = Source.UNKNOWN;

    }

    @Value
    @Builder
    public static class ProductInventoryConsumed implements ProductInventoryEvent {

        @NonNull
        ProductIdentifier productId;

        @NonNull
        Integer consumedQuantity;

        @NonNull
        Integer newQuantity;

        @NonNull
        @Builder.Default
        Source source = Source.UNKNOWN;

    }

    @Value
    @Builder
    public static class ProductInventoryCorrected implements ProductInventoryEvent {

        @NonNull
        ProductIdentifier productId;

        @NonNull
        Integer oldQuantity;

        @NonNull
        Integer newQuantity;

        @NonNull
        Principal principal;

    }

    @Value
    @Builder
    public static class ProductInventoryThresholdUnderrun implements ProductInventoryEvent {

        @NonNull
        ProductIdentifier productId;

        @NonNull
        Integer currentQuantity;

        @NonNull
        Integer threshold;

    }

    @Value
    @Builder
    public static class ProductInventoryEmptied implements ProductInventoryEvent {

        @NonNull
        ProductIdentifier productId;

        @NonNull
        Integer currentQuantity;

    }

}

