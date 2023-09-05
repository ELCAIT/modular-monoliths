package com.example.modularmonoliths.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;
import java.util.UUID;

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

import com.example.modularmonoliths.common.exception.EntityNotFoundException;
import com.example.modularmonoliths.common.type.Quantity;
import com.example.modularmonoliths.model.ProductionOrder;
import com.example.modularmonoliths.model.Product.ProductIdentifier;
import com.example.modularmonoliths.model.ProductionOrder.ProductionOrderState;
import com.example.modularmonoliths.repository.ProductionOrderRepository;
import com.example.modularmonoliths.service.ProductionOrderService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;

@RestController
@RequestMapping("/api/productionOrders")
@RequiredArgsConstructor
class ProductionOrderController implements RepresentationModelProcessor<EntityModel<ProductionOrder>> {

    static final String RESOURCE_REL = "productionOrders";
    static final String REL_CREATE = "create";
    static final String REL_RENAME = "rename";
    static final String REL_SUBMIT = "submit";
    static final String REL_ACCEPT = "accept";
    static final String REL_COMPLETE = "complete";

    private final ProductionOrderRepository productionOrders;
    private final ProductionOrderService productionOrderService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(
                CollectionModel.wrap(productionOrders.findAll())
                        .add(linkTo(methodOn(getClass()).create(null)).withRel(REL_CREATE)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findOne(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(
                EntityModel.of(productionOrders.findById(id)));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateRequest request) {
        return ResponseEntity.ok().body(EntityModel.of(productionOrderService.createOrder(
                request.getName(),
                request.getProductIdentifier(),
                request.getQuantityToProduce())));
    }

    @PostMapping("/{id}/rename")
    public ResponseEntity<?> rename(@PathVariable UUID id, @RequestBody RenameRequest request) {
        return productionOrders.findById(id)
                .map(po -> productionOrders.save(po.renameTo(request.newName)))
                .map(po -> ResponseEntity.ok().body(EntityModel.of(po)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submit(@PathVariable UUID id) {
        return productionOrders.findById(id)
                .map(po -> productionOrders.save(po.submit()))
                .map(po -> ResponseEntity.ok().body(EntityModel.of(po)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<?> accept(@PathVariable UUID id, @RequestBody AcceptRequest request) {
        return productionOrders.findById(id)
                .map(po -> productionOrders.save(po.accept(request.expectedCompletionDate)))
                .map(po -> ResponseEntity.ok().body(EntityModel.of(po)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<?> complete(@PathVariable UUID id) {
        return productionOrders.findById(id)
                .map(po -> productionOrders.save(po.complete()))
                .map(po -> ResponseEntity.ok().body(EntityModel.of(po)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public EntityModel<ProductionOrder> process(EntityModel<ProductionOrder> model) {
        val order = model.getContent();
        if (order == null) return model;
        if (order.getState() == ProductionOrderState.DRAFT) {
            model.add(linkTo(methodOn(getClass()).rename(order.getId(), null)).withRel(REL_RENAME));
            model.add(linkTo(methodOn(getClass()).submit(order.getId())).withRel(REL_SUBMIT));
        }
        if (order.getState() == ProductionOrderState.SUBMITTED) {
            model.add(linkTo(methodOn(getClass()).accept(order.getId(), null)).withRel(REL_ACCEPT));
        }
        if (order.getState() == ProductionOrderState.ACCEPTED) {
            model.add(linkTo(methodOn(getClass()).complete(order.getId())).withRel(REL_COMPLETE));
        }
        return model.add(linkTo(methodOn(getClass()).findOne(order.getId())).withSelfRel());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    void handleValidationException(Exception exception) {
        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
    }

    @ExceptionHandler({EntityNotFoundException.class})
    void handleNotFoundException(EntityNotFoundException exception) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @Value
    static class CreateRequest {

        @NonNull String name;
        @NonNull ProductIdentifier productIdentifier;
        @NonNull Quantity quantityToProduce;
    }

    @Value
    static class RenameRequest {

        @NonNull String newName;
    }

    @Value
    static class AcceptRequest {

        @NonNull LocalDate expectedCompletionDate;
    }

}
