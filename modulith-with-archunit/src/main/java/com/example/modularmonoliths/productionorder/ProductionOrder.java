package com.example.modularmonoliths.productionorder;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import com.example.modularmonoliths.common.exception.BusinessException;
import com.example.modularmonoliths.common.type.Quantity;
import com.example.modularmonoliths.masterdata.Product;
import com.example.modularmonoliths.productionorder.event.ProductionOrderCompleted;

import lombok.Getter;
import lombok.val;

@Getter
public class ProductionOrder extends AbstractAggregateRoot<ProductionOrder> {

	@Id
	private UUID id;
	@Version
	private Integer version;
	private String name;
	private LocalDate expectedCompletionDate;
	private LocalDate effectiveCompletionDate;
	private ProductionOrderState state;

	private AggregateReference<Product, UUID> product;
	private int quantityToProduce;

	public Quantity getQuantityToProduce() {
		return Quantity.of(quantityToProduce);
	}

	public Optional<LocalDate> getExpectedCompletionDate() {
		return Optional.ofNullable(expectedCompletionDate);
	}

	public Optional<LocalDate> getEffectiveCompletionDate() {
		return Optional.ofNullable(effectiveCompletionDate);
	}

	public static ProductionOrder create(String name, Product product, Quantity quantityToProduce) {
		if (quantityToProduce.intValue() <= 0) {
			throw new BusinessException("Quantity must be positive, but was " + quantityToProduce.intValue());
		}
		val result = new ProductionOrder();
		result.id = UUID.randomUUID();
		result.name = name;
		result.state = ProductionOrderState.DRAFT;
		result.product = AggregateReference.to(product.getId());
		result.quantityToProduce = quantityToProduce.intValue();
		return result;
	}
	
	public ProductionOrder renameTo(String newName) {
		if (state != ProductionOrderState.DRAFT) {
			throw new IllegalStateException("Cannot rename production order in state " + state);
		}
		name = newName;
		return this;
	}
		
	public ProductionOrder submit() {
		if (state != ProductionOrderState.DRAFT) {
			throw new IllegalStateException("Cannot submit production order in state " + state);
		}
		state = ProductionOrderState.SUBMITTED;
		return this;
	}

	public ProductionOrder accept(LocalDate expectedCompletionDate) {
		if (state != ProductionOrderState.SUBMITTED) {
			throw new IllegalStateException("Cannot accept production order in state " + state);
		}
		Objects.requireNonNull(expectedCompletionDate, "expectedCompletionDate is required to submit a production order");
		if (expectedCompletionDate.isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("Expected completion date must be in the future, but was " + expectedCompletionDate);
		}
		state = ProductionOrderState.ACCEPTED;
		this.expectedCompletionDate = expectedCompletionDate;
		return this;
	}

	public ProductionOrder complete() {
		if (state != ProductionOrderState.ACCEPTED) {
			throw new IllegalStateException("Cannot complete production order in state " + state);
		}
		state = ProductionOrderState.COMPLETED;
		effectiveCompletionDate = LocalDate.now();
		registerEvent(ProductionOrderCompleted.builder()
				.productionOrderId(id)
				.productId(product.getId())
				.producedQuantity(Quantity.of(quantityToProduce))
				.build());
		return this;
	}

	public enum ProductionOrderState { DRAFT, SUBMITTED, ACCEPTED, COMPLETED }

}
