package com.example.modularmonoliths.masterdata.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.UUID;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.modularmonoliths.common.log.LogPrefix;
import com.example.modularmonoliths.masterdata.Product;
import com.example.modularmonoliths.masterdata.Product.ProductState;
import com.example.modularmonoliths.masterdata.Products;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
class ProductController implements RepresentationModelProcessor<EntityModel<Product>> {

    static final String RESOURCE_REL = "products";
    static final String REL_CREATE = "create";
    static final String REL_RENAME = "rename";
    static final String REL_DISCONTINUE = "discontinue";

    private final Products products;

    @PostMapping
    ResponseEntity<?> create(@RequestBody CreateRequest request) {
        log.info("{} {}", LogPrefix.INCOMING, request);

        val result = Product.create(request.getName());
        products.save(result);
        return ResponseEntity
        		.created(getSelfLink(result).toUri())
        		.body(EntityModel.of(result));
    }

    @GetMapping("/{id}")
    ResponseEntity<?> findOne(@PathVariable UUID id) {
        return products.findById(id)
                .map(EntityModel::of)
                .map(ResponseEntity.ok()::body)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    ResponseEntity<?> findAll() {
        return ResponseEntity.ok(CollectionModel.wrap(products.findAll())
                .add(linkTo(methodOn(getClass()).create(null)).withRel(REL_CREATE)));
    }

    @PostMapping("/{id}/rename")
    ResponseEntity<?> rename(@PathVariable("id") Product product, @RequestBody RenameRequest request) {
        log.info("{} {}", LogPrefix.INCOMING, request);

        return saveAndConvertToResponse(
        		product.rename(request.getNewName()));
    }

    @PostMapping("/{id}/discontinue")
    ResponseEntity<?> discontinue(@PathVariable("id") Product product, DiscontinueRequest request) {
        log.info("{} {}", LogPrefix.INCOMING, request);

        return saveAndConvertToResponse(
        		product.discontinue());
    }
    
    ResponseEntity<?> saveAndConvertToResponse(Product product) {
    	return ResponseEntity.ok(
    			EntityModel.of(
    					products.save(product)));
    	
    }

    @Override
    public EntityModel<Product> process(EntityModel<Product> model) {
        val product = model.getContent();
        if (product == null) return model;
        model.add(getSelfLink(product));
        if (product.getState() == ProductState.ACTIVE) {
            model.add(linkTo(methodOn(getClass()).rename(product, null)).withRel(REL_RENAME));
            model.add(linkTo(methodOn(getClass()).discontinue(product, null)).withRel(REL_DISCONTINUE));
        }
        return model;
    }
    
    private Link getSelfLink(Product product) {
    	return linkTo(methodOn(getClass()).findOne(product.getId())).withSelfRel();
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    void handleValidationException(Exception exception) {
        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
    }

    @Value(staticConstructor = "of")
    static class CreateRequest {
        @NonNull String name;
    }

    @Value(staticConstructor = "of")
    static class RenameRequest {
        @NonNull String newName;
    }

    @Value
    static class DiscontinueRequest {
    }

}
