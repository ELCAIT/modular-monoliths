package com.example.modularmonoliths.web;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.modularmonoliths.common.type.Quantity;
import com.example.modularmonoliths.model.Product;
import com.example.modularmonoliths.model.ProductionOrder;
import com.example.modularmonoliths.model.ProductionOrder.ProductionOrderState;
import com.example.modularmonoliths.repository.ProductRepository;
import com.example.modularmonoliths.repository.ProductionOrderRepository;

import lombok.val;

@SpringBootTest
@AutoConfigureMockMvc
class ProductionOrderWebTests {

	@Autowired
	MockMvc mvc;

	@Autowired
	ProductionOrderRepository productionOrders;

	@Autowired
	ProductRepository products;

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
		mvc.perform(MockMvcRequestBuilders.get("/api/productionOrders").contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded['productionOrders']", Matchers.hasSize(2)));
	}

	@Test
	public void createOrder() throws Exception {
		val body = """
				    {
				        "name": "Test-Order",
				        "productIdentifier": "pid",
				        "quantityToProduce": 5
				    }
				""".replace("pid", product1.getId().uuidValue().toString());
		productionOrders.deleteAll();
		
		// act
		mvc.perform(post("/api/productionOrders")
				.contentType("application/json")
				.content(body))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._links['self']").exists())
				.andExpect(jsonPath("$._links['submit'].href").value(endsWith("/submit")))
				.andExpect(jsonPath("$._links['accept']").doesNotExist());
		
		// assert
		assertThat(productionOrders.findAll())
			.hasSize(1)
			.first()
				.hasFieldOrPropertyWithValue("name", "Test-Order")
				.hasFieldOrPropertyWithValue("product", product1)
				.hasFieldOrPropertyWithValue("quantityToProduce", Quantity.of(5))
				.hasFieldOrPropertyWithValue("state", ProductionOrderState.DRAFT);

	}

}
