package javaprac.service;

import javaprac.dao.ManufacturerDao;
import javaprac.dao.OrderItemDao;
import javaprac.dao.ProductAttributeValueDao;
import javaprac.dao.ProductDao;
import javaprac.dao.ProductTypeAttributeDao;
import javaprac.dao.ProductTypeDao;
import javaprac.model.Manufacturer;
import javaprac.model.Product;
import javaprac.model.ProductAttributeValue;
import javaprac.model.ProductType;
import javaprac.model.ProductTypeAttribute;
import javaprac.service.dto.StaffProductAttributeValueInput;
import javaprac.service.dto.StaffProductFormCommand;
import javaprac.service.dto.StaffProductFormData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StaffProductService {

    private final ProductDao productDao;
    private final ProductTypeDao productTypeDao;
    private final ManufacturerDao manufacturerDao;
    private final ProductTypeAttributeDao productTypeAttributeDao;
    private final ProductAttributeValueDao productAttributeValueDao;
    private final OrderItemDao orderItemDao;

    public StaffProductService(ProductDao productDao,
                               ProductTypeDao productTypeDao,
                               ManufacturerDao manufacturerDao,
                               ProductTypeAttributeDao productTypeAttributeDao,
                               ProductAttributeValueDao productAttributeValueDao,
                               OrderItemDao orderItemDao) {
        this.productDao = productDao;
        this.productTypeDao = productTypeDao;
        this.manufacturerDao = manufacturerDao;
        this.productTypeAttributeDao = productTypeAttributeDao;
        this.productAttributeValueDao = productAttributeValueDao;
        this.orderItemDao = orderItemDao;
    }


    @Transactional(readOnly = true)
    public StaffProductFormData buildCreateFormData() {
        return new StaffProductFormData(
                new Product(),
                productTypeDao.findAll(),
                manufacturerDao.findAll(),
                Collections.emptyList(),
                "create",
                null,
                null
        );
    }

    @Transactional(readOnly = true)
    public StaffProductFormData buildCreateFormData(Long typeId) {
        Product formProduct = new Product();

        if (typeId != null) {
            productTypeDao.findById(typeId).ifPresent(formProduct::setType);
        }

        return new StaffProductFormData(
                formProduct,
                productTypeDao.findAll(),
                manufacturerDao.findAll(),
                buildAttributeInputsForType(typeId, Collections.emptyMap()),
                "create",
                typeId,
                null
        );
    }

    @Transactional(readOnly = true)
    public StaffProductFormData buildEditFormData(Long id) {
        Product product = productDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + id));

        Long typeId = product.getType() != null ? product.getType().getId() : null;
        Long manufacturerId = product.getManufacturer() != null ? product.getManufacturer().getId() : null;

        Map<Long, String> existingValues = productAttributeValueDao.findByProductId(id).stream()
                .collect(Collectors.toMap(
                        v -> v.getAttribute().getId(),
                        ProductAttributeValue::getValue
                ));

        return new StaffProductFormData(
                product,
                productTypeDao.findAll(),
                manufacturerDao.findAll(),
                buildAttributeInputsForType(typeId, existingValues),
                "edit",
                typeId,
                manufacturerId
        );
    }

    @Transactional
    public void createProduct(StaffProductFormCommand cmd) {
        ProductType type = requireProductType(cmd.typeId());
        Manufacturer manufacturer = requireManufacturer(cmd.manufacturerId());

        Product product = new Product();
        product.setType(type);
        product.setManufacturer(manufacturer);
        product.setName(requireNonBlank(cmd.name()));
        product.setDescription(normalizeNullable(cmd.description()));
        product.setPrice(requirePrice(cmd));
        product.setStockQty(requireStockQty(cmd));

        productDao.save(product);

        saveAttributeValues(product, type.getId(), cmd.attributeIds(), cmd.attributeValues());
    }

    @Transactional
    public void updateProduct(Long id, StaffProductFormCommand cmd) {
        Product product = productDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + id));

        ProductType type = requireProductType(cmd.typeId());
        Manufacturer manufacturer = requireManufacturer(cmd.manufacturerId());

        product.setType(type);
        product.setManufacturer(manufacturer);
        product.setName(requireNonBlank(cmd.name()));
        product.setDescription(normalizeNullable(cmd.description()));
        product.setPrice(requirePrice(cmd));
        product.setStockQty(requireStockQty(cmd));

        productDao.update(product);

        productAttributeValueDao.deleteByProductId(product.getId());
        saveAttributeValues(product, type.getId(), cmd.attributeIds(), cmd.attributeValues());
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (orderItemDao.existsByProductId(id)) {
            throw new IllegalArgumentException("Нельзя удалить товар: товар присутствует в заказах");
        }

        productDao.deleteById(id);
    }

    @Transactional(readOnly = true)
    public StaffProductFormData buildCreateFormDataFromCommand(StaffProductFormCommand cmd) {
        Product formProduct = new Product();
        formProduct.setName(cmd.name());
        formProduct.setDescription(cmd.description());
        formProduct.setPrice(cmd.price());
        formProduct.setStockQty(cmd.stockQty());

        if (cmd.typeId() != null) {
            productTypeDao.findById(cmd.typeId()).ifPresent(formProduct::setType);
        }
        if (cmd.manufacturerId() != null) {
            manufacturerDao.findById(cmd.manufacturerId()).ifPresent(formProduct::setManufacturer);
        }

        return new StaffProductFormData(
                formProduct,
                productTypeDao.findAll(),
                manufacturerDao.findAll(),
                buildAttributeInputsFromCommand(cmd.typeId(), cmd.attributeIds(), cmd.attributeValues()),
                "create",
                cmd.typeId(),
                cmd.manufacturerId()
        );
    }

    @Transactional(readOnly = true)
    public StaffProductFormData buildEditFormDataFromCommand(Long id, StaffProductFormCommand cmd) {
        Product formProduct = new Product();
        formProduct.setId(id);
        formProduct.setName(cmd.name());
        formProduct.setDescription(cmd.description());
        formProduct.setPrice(cmd.price());
        formProduct.setStockQty(cmd.stockQty());

        if (cmd.typeId() != null) {
            productTypeDao.findById(cmd.typeId()).ifPresent(formProduct::setType);
        }
        if (cmd.manufacturerId() != null) {
            manufacturerDao.findById(cmd.manufacturerId()).ifPresent(formProduct::setManufacturer);
        }

        return new StaffProductFormData(
                formProduct,
                productTypeDao.findAll(),
                manufacturerDao.findAll(),
                buildAttributeInputsFromCommand(cmd.typeId(), cmd.attributeIds(), cmd.attributeValues()),
                "edit",
                cmd.typeId(),
                cmd.manufacturerId()
        );
    }

    private ProductType requireProductType(Long typeId) {
        if (typeId == null) {
            throw new IllegalArgumentException("Выберите тип товара");
        }

        return productTypeDao.findById(typeId)
                .orElseThrow(() -> new IllegalArgumentException("Тип товара не найден: " + typeId));
    }

    private Manufacturer requireManufacturer(Long manufacturerId) {
        if (manufacturerId == null) {
            throw new IllegalArgumentException("Выберите производителя");
        }

        return manufacturerDao.findById(manufacturerId)
                .orElseThrow(() -> new IllegalArgumentException("Производитель не найден: " + manufacturerId));
    }

    private java.math.BigDecimal requirePrice(StaffProductFormCommand cmd) {
        if (cmd.price() == null) {
            throw new IllegalArgumentException("Цена обязательна");
        }
        if (cmd.price().signum() < 0) {
            throw new IllegalArgumentException("Цена не может быть отрицательной");
        }
        return cmd.price();
    }

    private Integer requireStockQty(StaffProductFormCommand cmd) {
        if (cmd.stockQty() == null) {
            throw new IllegalArgumentException("Остаток обязателен");
        }
        if (cmd.stockQty() < 0) {
            throw new IllegalArgumentException("Остаток не может быть отрицательным");
        }
        return cmd.stockQty();
    }

    private void saveAttributeValues(Product product,
                                     Long productTypeId,
                                     List<Long> attributeIds,
                                     List<String> attributeValues) {

        List<ProductTypeAttribute> allowedAttributes = productTypeAttributeDao.findByProductTypeId(productTypeId);
        Map<Long, ProductTypeAttribute> allowedById = allowedAttributes.stream()
                .collect(Collectors.toMap(ProductTypeAttribute::getId, Function.identity()));

        Map<Long, String> submitted = pairSubmittedValues(attributeIds, attributeValues);

        for (Long submittedAttributeId : submitted.keySet()) {
            if (!allowedById.containsKey(submittedAttributeId)) {
                throw new IllegalArgumentException("Характеристика не принадлежит выбранному типу товара: " + submittedAttributeId);
            }
        }

        for (ProductTypeAttribute attribute : allowedAttributes) {
            String value = normalizeNullable(submitted.get(attribute.getId()));

            if (attribute.isRequired() && (value == null || value.isBlank())) {
                throw new IllegalArgumentException("Обязательная характеристика не заполнена: " + attribute.getName());
            }

            if (value == null) {
                continue;
            }

            ProductAttributeValue pav = new ProductAttributeValue();
            pav.setProduct(product);
            pav.setAttribute(attribute);
            pav.setValue(value);
            productAttributeValueDao.save(pav);
        }
    }

    private Map<Long, String> pairSubmittedValues(List<Long> attributeIds, List<String> attributeValues) {
        Map<Long, String> result = new LinkedHashMap<>();

        if (attributeIds == null || attributeValues == null) {
            return result;
        }

        int n = Math.min(attributeIds.size(), attributeValues.size());

        for (int i = 0; i < n; i++) {
            Long attributeId = attributeIds.get(i);
            if (attributeId == null) {
                continue;
            }

            result.put(attributeId, attributeValues.get(i));
        }

        return result;
    }

    private List<StaffProductAttributeValueInput> buildAttributeInputsForType(Long typeId, Map<Long, String> valuesByAttributeId) {
        if (typeId == null) {
            return Collections.emptyList();
        }

        return productTypeAttributeDao.findByProductTypeId(typeId).stream()
                .map(attribute -> new StaffProductAttributeValueInput(
                        attribute.getId(),
                        attribute.getName(),
                        attribute.getSortOrder(),
                        attribute.isRequired(),
                        valuesByAttributeId.get(attribute.getId())
                ))
                .toList();
    }

    private List<StaffProductAttributeValueInput> buildAttributeInputsFromCommand(Long typeId,
                                                                                  List<Long> attributeIds,
                                                                                  List<String> attributeValues) {
        Map<Long, String> valuesByAttributeId = pairSubmittedValues(attributeIds, attributeValues);
        return buildAttributeInputsForType(typeId, valuesByAttributeId);
    }

    private String requireNonBlank(String value) {
        String normalized = normalizeNullable(value);
        if (normalized == null) {
            throw new IllegalArgumentException("Название товара обязательно");
        }
        return normalized;
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    @Transactional(readOnly = true)
    public java.util.List<StaffProductAttributeValueInput> getAttributeInputsForType(Long typeId) {
        return buildAttributeInputsForType(typeId, java.util.Collections.emptyMap());
    }
}