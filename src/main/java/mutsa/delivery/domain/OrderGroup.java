package mutsa.delivery.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public static OrderGroup createOrderGroup(User user, Address address, List<CartItem> cartItems){
        OrderGroup orderGroup = OrderGroup.builder()
                .user(user)
                .address(address.getAddress())
                .addressName(address.getAddressName())
                .phone(address.getPhoneNumber())
                .createdAt(LocalDateTime.now())
                .orders(new ArrayList<>()) // 리스트 초기화
                .build();

        Map<Shop, List<CartItem>> itemsByShop = cartItems.stream()
                .collect(Collectors.groupingBy(item -> item.getMenu().getShop()));

        for (Map.Entry<Shop, List<CartItem>> entry : itemsByShop.entrySet()) {
            Order order = Order.createOrder(user, entry.getKey(), orderGroup, entry.getValue());
            orderGroup.getOrders().add(order); // 내 리스트에 담음
        }

        orderGroup.totalPrice = orderGroup.getOrders().stream()
                .mapToLong(Order::getTotalPrice)
                .sum();

        return orderGroup;
    }
}
