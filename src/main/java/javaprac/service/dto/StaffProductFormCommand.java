package javaprac.service.dto;

import java.math.BigDecimal;
import java.util.List;

public record StaffProductFormCommand(
        Long typeId,
        Long manufacturerId,
        String name,
        String description,
        BigDecimal price,
        Integer stockQty,
        List<Long> attributeIds,
        List<String> attributeValues
) {
}