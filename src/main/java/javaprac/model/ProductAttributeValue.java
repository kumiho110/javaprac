package javaprac.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
        name = "product_attribute_value",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"product_id", "product_type_attribute_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"product", "attribute"})
public class ProductAttributeValue implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_type_attribute_id", nullable = false)
    private ProductTypeAttribute attribute;

    @Column(name = "value", nullable = false, length = 255)
    private String value;
}