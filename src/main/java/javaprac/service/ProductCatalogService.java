package javaprac.service;

import javaprac.dao.ManufacturerDao;
import javaprac.dao.ProductDao;
import javaprac.dao.ProductTypeAttributeDao;
import javaprac.dao.ProductTypeDao;
import javaprac.model.Manufacturer;
import javaprac.model.Product;
import javaprac.model.ProductAttributeValue;
import javaprac.model.ProductType;
import javaprac.model.ProductTypeAttribute;
import javaprac.service.dto.ProductAttributeRow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductCatalogService {

    private final ProductDao productDao;
    private final ProductTypeDao productTypeDao;
    private final ManufacturerDao manufacturerDao;
    private final ProductTypeAttributeDao productTypeAttributeDao;

    public ProductCatalogService(ProductDao productDao,
                                 ProductTypeDao productTypeDao,
                                 ManufacturerDao manufacturerDao,
                                 ProductTypeAttributeDao productTypeAttributeDao) {
        this.productDao = productDao;
        this.productTypeDao = productTypeDao;
        this.manufacturerDao = manufacturerDao;
        this.productTypeAttributeDao = productTypeAttributeDao;
    }

    @Transactional(readOnly = true)
    public List<Product> search(String type, String manufacturer, String attributeName, String attributeValue) {
        return productDao.search(type, manufacturer, attributeName, attributeValue);
    }

    @Transactional(readOnly = true)
    public Product getProductDetails(Long id) {
        return productDao.findByIdWithAttributes(id)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + id));
    }

    @Transactional(readOnly = true)
    public List<ProductAttributeRow> buildAttributeRows(Product product) {
        if (product == null || product.getType() == null || product.getType().getId() == null) {
            return List.of();
        }

        Long typeId = product.getType().getId();
        List<ProductTypeAttribute> attrs = productTypeAttributeDao.findByProductTypeId(typeId);

        Map<Long, String> existing = new HashMap<>();
        if (product.getAttributeValues() != null) {
            for (ProductAttributeValue v : product.getAttributeValues()) {
                if (v == null || v.getAttribute() == null || v.getAttribute().getId() == null) {
                    continue;
                }
                existing.put(v.getAttribute().getId(), v.getValue());
            }
        }

        return attrs.stream()
                .map(a -> {
                    String raw = existing.get(a.getId());
                    String value = raw == null ? null : raw.trim();

                    if (a.isRequired()) {
                        return new ProductAttributeRow(
                                a.getName(),
                                (value == null || value.isBlank()) ? "Не указано" : value,
                                true
                        );
                    }

                    if (value == null || value.isBlank()) {
                        return null;
                    }

                    return new ProductAttributeRow(a.getName(), value, false);
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductType> listTypes() {
        return productTypeDao.findAll();
    }

    @Transactional(readOnly = true)
    public List<Manufacturer> listManufacturers() {
        return manufacturerDao.findAll();
    }

    @Transactional(readOnly = true)
    public List<String> listAttributeNamesByType(String type) {
        return productDao.listAttributeNamesByType(type);
    }

    @Transactional(readOnly = true)
    public List<String> listAttributeValuesByTypeAndName(String type, String attributeName) {
        return productDao.listAttributeValuesByTypeAndName(type, attributeName);
    }
}