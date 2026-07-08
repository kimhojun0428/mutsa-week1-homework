package mutsa.delivery.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",  nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id",  nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_group_id")
    private OrderGroup orderGroup;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Long getTotalPrice(){
        return this.orderItems.stream().mapToLong(OrderItem::getTotalPrice).sum();
    }

    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCELED;
    }

    public static Order createOrder(User user, Shop shop, OrderGroup orderGroup, List<CartItem> shopCartItems){
        Order order = Order.builder()
                .user(user)
                .shop(shop)
                .orderGroup(orderGroup)
                .orderStatus(OrderStatus.ORDERED)
                .orderItems(new ArrayList<>())
                .build();

        for (CartItem cartItem : shopCartItems){
            OrderItem orderItem = OrderItem.createOrderItem(order, cartItem);
            order.getOrderItems().add(orderItem);
        }
        return order;
    }
}
