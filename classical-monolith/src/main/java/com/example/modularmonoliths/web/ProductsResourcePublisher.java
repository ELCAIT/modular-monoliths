package com.example.modularmonoliths.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
class ProductsResourcePublisher implements RepresentationModelProcessor<RepositoryLinksResource> {

    @Override
    public RepositoryLinksResource process(RepositoryLinksResource linksResource) {
        return linksResource.add(linkTo(methodOn(ProductController.class).findAll()).withRel(ProductController.RESOURCE_REL));
    }

}
