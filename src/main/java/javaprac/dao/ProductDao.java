package javaprac.dao;

import javaprac.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao extends CommonDao<Product, Long> {

    List<Product> findAllWithRefs();

    Optional<Product> findByIdWithAttributes(Long id);

    Product findByIdForUpdate(Long id);

    boolean existsByTypeId(Long typeId);

    boolean existsByManufacturerId(Long manufacturerId);

    List<Product> search(String typeName, String manufacturerName, String attributeName, String attributeValue);

    List<String> listAttributeNamesByType(String typeName);

    List<String> listAttributeValuesByTypeAndName(String typeName, String attributeName);
}