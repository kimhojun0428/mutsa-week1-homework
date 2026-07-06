package mutsa.delivery.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class OrderGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Long totalPrice;
    private LocalDateTime createdAt;
    private String address;
    private String addressName;
    private String phone;

    @OneToMany(mappedBy = "orderGroup", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    public Long getTotalPrice() {
        return this.orders.stream().mapToLong(Order::getTotalPrice).sum();
    }

    public void cancelOrders() {
        this.orders.forEach(Order::cancelOrder);
    }

}
