package javaprac.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "manufacturer",
        uniqueConstraints = @UniqueConstraint(name = "uq_manufacturer_name_country", columnNames = {"name", "assembly_country"})
)
@Getter
@Setter
public class Manufacturer implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "assembly_country", nullable = false, length = 100)
    private String assemblyCountry;
}
