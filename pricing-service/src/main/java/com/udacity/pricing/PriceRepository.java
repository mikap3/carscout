package com.udacity.pricing;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "price", path = "price")
public interface PriceRepository extends CrudRepository<Price, Long> {

}
