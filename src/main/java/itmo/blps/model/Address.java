package itmo.blps.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table(name = "address")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private Integer building;

    @Column(nullable = false)
    private Integer entrance;

    @Column(nullable = false)
    private Integer floor;

    @Column(nullable = false)
    private Integer flat;

    @ManyToMany(mappedBy = "addresses")
    @JsonBackReference
    private List<User> users;
}
