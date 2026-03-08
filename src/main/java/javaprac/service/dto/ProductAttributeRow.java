package javaprac.service.dto;

public record ProductAttributeRow(
        String name,
        String value,
        boolean required
) {
}
