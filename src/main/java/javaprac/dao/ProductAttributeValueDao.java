package javaprac.dao;

import javaprac.model.ProductAttributeValue;

import java.util.List;
import java.util.Optional;

public interface ProductAttributeValueDao extends CommonDao<ProductAttributeValue, Long> {
    List<ProductAttributeValue> findByProductId(Long productId);
    void deleteByProductId(Long productId);
}