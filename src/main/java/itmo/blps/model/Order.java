package itmo.blps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;

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

    @Column(nullable = false)
    private LocalDateTime creationTime = now();
}
