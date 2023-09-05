package com.example.modularmonoliths.productionorder.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.modularmonoliths.masterdata.Product;
import com.example.modularmonoliths.masterdata.Product.ProductIdentifier;
import com.example.modularmonoliths.masterdata.Product.ProductState;
import com.example.modularmonoliths.productionorder.ProductionOrder;
import com.example.modularmonoliths.productionorder.ProductionOrderService;
import com.example.modularmonoliths.productionorder.web.ProductionOrderController.CreateRequest;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
class ProductExtensionsController implements RepresentationModelProcessor<EntityModel<Product>> {

	public static final String REL_CREATE_ORDER = "createOrder";

	private final ProductionOrderService productionOrderService;
	
    @PostMapping("/{id}/createOrder")
    public ResponseEntity<?> create(@PathVariable("id") ProductIdentifier productId, @RequestBody CreateRequest request) {
    	val order = productionOrderService.createOrder(
                request.getOrderName(),
        		productId,
                request.getQuantityToProduce());
        return ResponseEntity
        		.created(getSelfLink(order).toUri())
        		.body(EntityModel.of(order));
    }

    private Link getSelfLink(ProductionOrder order) {
    	return linkTo(methodOn(ProductionOrderController.class)
    			.findOne(order.getId()))
    			.withSelfRel();
    }
	
	@Override
	public EntityModel<Product> process(EntityModel<Product> model) {
		val product = model.getContent();
		if (product == null) return model;
		if (product.getState() == ProductState.ACTIVE) {
            model.add(linkTo(methodOn(ProductExtensionsController.class)
            		.create(product.getId(), null)).withRel(REL_CREATE_ORDER));
		}
		return model;
	}
		
}
