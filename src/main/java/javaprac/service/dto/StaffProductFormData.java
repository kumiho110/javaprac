package javaprac.service.dto;

import javaprac.model.Manufacturer;
import javaprac.model.Product;
import javaprac.model.ProductType;

import java.util.List;

public record StaffProductFormData(
        Product product,
        List<ProductType> types,
        List<Manufacturer> manufacturers,
        List<StaffProductAttributeValueInput> attributes,
        String formMode,
        Long qTypeId,
        Long qManufacturerId
) {
}