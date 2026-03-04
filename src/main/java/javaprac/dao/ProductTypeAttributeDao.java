package javaprac.dao;

import javaprac.model.ProductTypeAttribute;

import java.util.List;

public interface ProductTypeAttributeDao extends CommonDao<ProductTypeAttribute, Long> {
    List<ProductTypeAttribute> findByProductTypeId(Long productTypeId);
}