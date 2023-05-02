package com.example.modularmonoliths;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

import lombok.val;

@SpringBootTest
@AutoConfigureMockMvc
class ModularMonolithsApplicationTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Test
	void productionProductionOrderEndToEnd() throws Exception {

		val productJson = mockMvc.perform(post("/api/products")
				.contentType("application/json")
				.content("""
						{
							"name": "Product 1"
						}
						"""))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$._links['createOrder']").exists())
				.andReturn()
				.getResponse().getContentAsString();

		val createOrderLink = JsonPath.compile("$._links['createOrder'].href").read(productJson).toString();			
		
		var orderJson = mockMvc.perform(post(createOrderLink)
				.contentType("application/json")
				.content("""
						{
							"orderName": "Order 1",
							"quantityToProduce": 7
						}
						"""))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$._links['submit']").exists())
				.andExpect(jsonPath("$._links['accept']").doesNotExist())
				.andReturn()
				.getResponse().getContentAsString();
		
		val submitOrderLink = JsonPath.compile("$._links['submit'].href").read(orderJson).toString();			

		orderJson = mockMvc.perform(post(submitOrderLink)
				.contentType("application/json")
				.content("{}"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._links['submit']").doesNotExist())
				.andExpect(jsonPath("$._links['accept']").exists())
				.andExpect(jsonPath("$._links['complete']").doesNotExist())
				.andReturn()
				.getResponse().getContentAsString();
		
		val acceptOrderLink = JsonPath.compile("$._links['accept'].href").read(orderJson).toString();			

		orderJson = mockMvc.perform(post(acceptOrderLink)
				.contentType("application/json")
				.content("""
						{
							"expectedCompletionDate": "%s"
						}
						""".formatted(LocalDate.now().plusDays(3))))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._links['submit']").doesNotExist())
				.andExpect(jsonPath("$._links['accept']").doesNotExist())
				.andExpect(jsonPath("$._links['complete']").exists())
				.andReturn()
				.getResponse().getContentAsString();
		
		val completeOrderLink = JsonPath.compile("$._links['complete'].href").read(orderJson).toString();

		orderJson = mockMvc.perform(post(completeOrderLink)
				.contentType("application/json")
				.content("{}"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._links['self']").exists())
				.andExpect(jsonPath("$._links['submit']").doesNotExist())
				.andExpect(jsonPath("$._links['accept']").doesNotExist())
				.andExpect(jsonPath("$._links['complete']").doesNotExist())
				.andReturn()
				.getResponse().getContentAsString();

		mockMvc.perform(get("/api/productInventories"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded['productInventories'][0].currentQuantity").value(is(equalTo(7))))
				.andReturn()
				.getResponse().getContentAsString();
	}

}
