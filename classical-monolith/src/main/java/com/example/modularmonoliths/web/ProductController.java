package com.example.modularmonoliths.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.UUID;
import java.util.function.UnaryOperator;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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
import com.example.modularmonoliths.model.Product;
import com.example.modularmonoliths.model.Product.ProductIdentifier;
import com.example.modularmonoliths.model.Product.ProductState;
import com.example.modularmonoliths.repository.ProductRepository;

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

    private final ProductRepository products;

    @PostMapping
    ResponseEntity<?> create(@RequestBody CreateRequest request) {
        log.info("{} {}", LogPrefix.INCOMING, request);

        val result = Product.create(request.getName());
        products.save(result);
        return ResponseEntity.ok(EntityModel.of(result));
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
    ResponseEntity<?> rename(@PathVariable ProductIdentifier id, @RequestBody RenameRequest request) {
        log.info("{} {}", LogPrefix.INCOMING, request);

        return updateProduct(id, it -> it.rename(request.getNewName()));
    }

    @PostMapping("/{id}/discontinue")
    ResponseEntity<?> discontinue(@PathVariable ProductIdentifier id, DiscontinueRequest request) {
        log.info("{} {}", LogPrefix.INCOMING, request);

        return updateProduct(id, Product::discontinue);
    }

    private ResponseEntity<?> updateProduct(ProductIdentifier productId, UnaryOperator<Product> action) {
        return products.findById(productId.uuidValue())
                .map(action::apply)
                .map(products::save)
                .map(ResponseEntity.ok()::body)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public EntityModel<Product> process(EntityModel<Product> model) {
        val product = model.getContent();
        model.add(linkTo(methodOn(getClass()).findOne(product.getId().uuidValue())).withSelfRel());
        if (product.getState() == ProductState.ACTIVE) {
            model.add(linkTo(methodOn(getClass()).rename(product.getId(), null)).withRel(REL_RENAME));
            model.add(linkTo(methodOn(getClass()).discontinue(product.getId(), null)).withRel(REL_DISCONTINUE));
        }
        return model;
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
