package itmo.blps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import itmo.blps.dto.response.ProductResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double cost;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order")
    private List<Product> products = new ArrayList<>();

    @Column(name = "delivery_time")
    private String deliveryTime;

    @Column(name = "utensils_count")
    private Integer utensilsCount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(nullable = true, unique = true)
    private String sessionId;

    @Column(nullable = false)
    private Boolean isConfirmed = false;

    @Column(nullable = false)
    private Boolean isPaid = false;
}
