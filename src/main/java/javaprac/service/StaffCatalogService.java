package javaprac.service;

import javaprac.dao.ManufacturerDao;
import javaprac.dao.ProductAttributeValueDao;
import javaprac.dao.ProductDao;
import javaprac.dao.ProductTypeAttributeDao;
import javaprac.dao.ProductTypeDao;
import javaprac.model.Manufacturer;
import javaprac.model.ProductType;
import javaprac.model.ProductTypeAttribute;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StaffCatalogService {

    private final ProductTypeDao productTypeDao;
    private final ManufacturerDao manufacturerDao;
    private final ProductTypeAttributeDao productTypeAttributeDao;
    private final ProductDao productDao;
    private final ProductAttributeValueDao productAttributeValueDao;

    public StaffCatalogService(ProductTypeDao productTypeDao,
                               ManufacturerDao manufacturerDao,
                               ProductTypeAttributeDao productTypeAttributeDao,
                               ProductDao productDao,
                               ProductAttributeValueDao productAttributeValueDao) {
        this.productTypeDao = productTypeDao;
        this.manufacturerDao = manufacturerDao;
        this.productTypeAttributeDao = productTypeAttributeDao;
        this.productDao = productDao;
        this.productAttributeValueDao = productAttributeValueDao;
    }

    @Transactional(readOnly = true)
    public List<ProductType> listTypes() {
        return productTypeDao.findAll();
    }

    @Transactional(readOnly = true)
    public ProductType getType(Long id) {
        return productTypeDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Тип товара не найден: " + id));
    }

    @Transactional
    public void createType(String name) {
        String n = normalizeRequired(name, "Название типа товара обязательно");

        productTypeDao.findByNameIgnoreCase(n).ifPresent(existing -> {
            throw new IllegalArgumentException("Такой тип товара уже существует");
        });

        ProductType t = new ProductType();
        t.setName(n);
        productTypeDao.save(t);
    }

    @Transactional
    public void updateType(Long id, String name) {
        ProductType t = getType(id);
        String n = normalizeRequired(name, "Название типа товара обязательно");

        productTypeDao.findByNameIgnoreCase(n).ifPresent(existing -> {
            if (!existing.getId().equals(t.getId())) {
                throw new IllegalArgumentException("Такой тип товара уже существует");
            }
        });

        t.setName(n);
    }

    @Transactional
    public void deleteType(Long id) {
        ProductType t = productTypeDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Тип товара не найден: " + id));

        if (productDao.existsByTypeId(id)) {
            throw new IllegalArgumentException("Нельзя удалить тип товара: к нему уже привязаны товары");
        }

        productTypeDao.delete(t);
    }

    @Transactional(readOnly = true)
    public List<Manufacturer> listManufacturers() {
        return manufacturerDao.findAll();
    }

    @Transactional(readOnly = true)
    public Manufacturer getManufacturer(Long id) {
        return manufacturerDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Производитель не найден: " + id));
    }

    @Transactional
    public void createManufacturer(String name, String assemblyCountry) {
        String n = normalizeRequired(name, "Название производителя обязательно");
        String c = normalizeRequired(assemblyCountry, "Страна сборки обязательна");

        manufacturerDao.findByNameAndAssemblyCountryIgnoreCase(n, c).ifPresent(existing -> {
            throw new IllegalArgumentException("Такой производитель уже существует (название + страна сборки)");
        });

        Manufacturer m = new Manufacturer();
        m.setName(n);
        m.setAssemblyCountry(c);
        manufacturerDao.save(m);
    }

    @Transactional
    public void updateManufacturer(Long id, String name, String assemblyCountry) {
        Manufacturer m = getManufacturer(id);

        String n = normalizeRequired(name, "Название производителя обязательно");
        String c = normalizeRequired(assemblyCountry, "Страна сборки обязательна");

        manufacturerDao.findByNameAndAssemblyCountryIgnoreCase(n, c).ifPresent(existing -> {
            if (!existing.getId().equals(m.getId())) {
                throw new IllegalArgumentException("Такой производитель уже существует (название + страна сборки)");
            }
        });

        m.setName(n);
        m.setAssemblyCountry(c);
    }

    @Transactional
    public void deleteManufacturer(Long id) {
        Manufacturer m = getManufacturer(id);

        if (productDao.existsByManufacturerId(id)) {
            throw new IllegalArgumentException("Нельзя удалить производителя: к нему уже привязаны товары");
        }

        manufacturerDao.delete(m);
    }

    @Transactional(readOnly = true)
    public List<ProductTypeAttribute> listTypeAttributes(Long typeId) {
        return productTypeAttributeDao.findByProductTypeId(typeId);
    }

    @Transactional(readOnly = true)
    public ProductTypeAttribute getTypeAttribute(Long id) {
        ProductTypeAttribute a = productTypeAttributeDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Характеристика не найдена: " + id));

        a.getProductType().getName();
        return a;
    }

    @Transactional
    public void createTypeAttribute(Long typeId, String name, boolean required) {
        ProductType t = getType(typeId);
        String n = normalizeRequired(name, "Название характеристики обязательно");

        productTypeAttributeDao.findByProductTypeIdAndNameIgnoreCase(typeId, n).ifPresent(existing -> {
            throw new IllegalArgumentException("Такая характеристика уже существует для этого типа товара");
        });

        int maxSortOrder = productTypeAttributeDao.findMaxSortOrderByProductTypeId(typeId);

        ProductTypeAttribute a = new ProductTypeAttribute();
        a.setProductType(t);
        a.setName(n);
        a.setSortOrder(maxSortOrder + 10);
        a.setRequired(required);

        productTypeAttributeDao.save(a);
    }

    @Transactional
    public void updateTypeAttribute(Long id, String name, boolean required) {
        ProductTypeAttribute a = getTypeAttribute(id);
        Long typeId = a.getProductType().getId();

        String n = normalizeRequired(name, "Название характеристики обязательно");

        productTypeAttributeDao.findByProductTypeIdAndNameIgnoreCase(typeId, n).ifPresent(existing -> {
            if (!existing.getId().equals(a.getId())) {
                throw new IllegalArgumentException("Такая характеристика уже существует для этого типа товара");
            }
        });

        a.setName(n);
        a.setRequired(required);
    }

    @Transactional
    public void moveTypeAttributeUp(Long id) {
        ProductTypeAttribute current = getTypeAttribute(id);
        List<ProductTypeAttribute> attrs = productTypeAttributeDao.findByProductTypeId(current.getProductType().getId());

        int index = findAttributeIndex(attrs, id);
        if (index <= 0) {
            return;
        }

        ProductTypeAttribute previous = attrs.get(index - 1);
        swapSortOrder(previous, current);
    }

    @Transactional
    public void moveTypeAttributeDown(Long id) {
        ProductTypeAttribute current = getTypeAttribute(id);
        List<ProductTypeAttribute> attrs = productTypeAttributeDao.findByProductTypeId(current.getProductType().getId());

        int index = findAttributeIndex(attrs, id);
        if (index < 0 || index >= attrs.size() - 1) {
            return;
        }

        ProductTypeAttribute next = attrs.get(index + 1);
        swapSortOrder(current, next);
    }

    @Transactional
    public void deleteTypeAttribute(Long id) {
        ProductTypeAttribute a = getTypeAttribute(id);

        if (productAttributeValueDao.existsByAttributeId(id)) {
            throw new IllegalArgumentException("Нельзя удалить характеристику: она уже используется в товарах");
        }

        productTypeAttributeDao.delete(a);
    }

    private int findAttributeIndex(List<ProductTypeAttribute> attrs, Long id) {
        for (int i = 0; i < attrs.size(); i++) {
            if (attrs.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private void swapSortOrder(ProductTypeAttribute left, ProductTypeAttribute right) {
        int tmp = left.getSortOrder();
        left.setSortOrder(right.getSortOrder());
        right.setSortOrder(tmp);
    }

    private String normalizeRequired(String value, String err) {
        String v = value == null ? "" : value.trim();
        if (v.isBlank()) {
            throw new IllegalArgumentException(err);
        }
        return v;
    }
}