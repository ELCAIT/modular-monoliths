package com.example.modularmonoliths.masterdata.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.example.modularmonoliths.masterdata.Product;
import com.example.modularmonoliths.masterdata.Products;

import lombok.val;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private Products products;
	
	@Test
	void create_validRequest_createdWithLinks() throws Exception {

		mockMvc.perform(post("/api/products").contentType("application/json")
				.content("""
					{
						"name": "Product 1"
					}
				"""))
			.andExpect(status().isCreated())
					.andExpect(jsonPath("$._links['self']").exists())
					.andExpect(jsonPath("$._links['rename']").exists())
					.andExpect(jsonPath("$._links['discontinue']").exists());
	}

	@Test
	void rename_validRequest_renamed() throws Exception {
		val id = products.save(Product.create("Product 1")).getId();

		// act & assert
		mockMvc.perform(post("/api/products/" + id + "/rename")
				.contentType("application/json")
				.content("""
					{
						"newName": "Product 1B"
					}
				"""))
			.andExpect(status().isOk())
					.andExpect(jsonPath("$.name").value(Matchers.is(Matchers.equalTo("Product 1B"))))
					.andExpect(jsonPath("$._links['self']").exists())
					.andExpect(jsonPath("$._links['rename']").exists())
					.andExpect(jsonPath("$._links['discontinue']").exists());
	}
}
