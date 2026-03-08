package javaprac.dao;

import javaprac.model.ProductTypeAttribute;

import java.util.List;
import java.util.Optional;

public interface ProductTypeAttributeDao extends CommonDao<ProductTypeAttribute, Long> {
    List<ProductTypeAttribute> findByProductTypeId(Long productTypeId);

    Optional<ProductTypeAttribute> findByProductTypeIdAndNameIgnoreCase(Long productTypeId, String name);
    int findMaxSortOrderByProductTypeId(Long productTypeId);
}