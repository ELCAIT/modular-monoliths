package com.example.modularmonoliths.productionorder.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.modularmonoliths.common.type.Quantity;
import com.example.modularmonoliths.masterdata.Product;
import com.example.modularmonoliths.masterdata.Products;
import com.example.modularmonoliths.productionorder.ProductionOrder;
import com.example.modularmonoliths.productionorder.ProductionOrder.ProductionOrderState;
import com.example.modularmonoliths.productionorder.ProductionOrders;

import lombok.val;

@SpringBootTest
@AutoConfigureMockMvc
class ProductionOrderModuleTests {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ProductionOrders productionOrders;

	@Autowired
	Products products;

	Product product1 = Product.create("Product 1");
	Product product2 = Product.create("Product 2");

	@BeforeEach
	void initMasterdata() {
		products.save(product1);
		products.save(product2);
	}

	@Test
	void getAllProductionOrders() throws Exception {
		productionOrders.deleteAll();

		val productionOrder1 = ProductionOrder.create("ProductionOrder 1", product1, Quantity.of(5));
		productionOrders.save(productionOrder1);

		val productionOrder2 = ProductionOrder.create("ProductionOrder 2", product2, Quantity.of(10));
		productionOrders.save(productionOrder2);

		// act
		mockMvc.perform(MockMvcRequestBuilders.get("/api/productionOrders").contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded['productionOrders']", Matchers.hasSize(2)));
	}

	@Test
	public void createOrder() throws Exception {
		productionOrders.deleteAll();
		
		// act
		mockMvc.perform(post("/api/products/" + product1.getId() + "/createOrder")
				.contentType("application/json")
				.content("""
				    {
				        "orderName": "Test-Order",
				        "quantityToProduce": 5
				    }
				"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$._links['self']").exists())
				.andExpect(jsonPath("$._links['submit'].href").value(endsWith("/submit")))
				.andExpect(jsonPath("$._links['accept']").doesNotExist());
		
		// assert
		assertThat(productionOrders.findAll())
			.hasSize(1)
			.first()
				.hasFieldOrPropertyWithValue("name", "Test-Order")
				.hasFieldOrPropertyWithValue("product", AggregateReference.to(product1.getId()))
				.hasFieldOrPropertyWithValue("quantityToProduce", Quantity.of(5))
				.hasFieldOrPropertyWithValue("state", ProductionOrderState.DRAFT);

	}

}
