package javaprac.dao;

import javaprac.model.ProductType;

import java.util.Optional;

public interface ProductTypeDao extends CommonDao<ProductType, Long> {

    Optional<ProductType> findByNameIgnoreCase(String name);
}