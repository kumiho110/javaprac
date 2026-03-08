package javaprac.service.dto;

public record StaffProductAttributeValueInput(
        Long attributeId,
        String attributeName,
        Integer sortOrder,
        boolean required,
        String value
) {
}