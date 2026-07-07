package mutsa.delivery.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.awt.*;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    private String menuName;
    private String description;
    private Long unitPrice;
    private int quantity;
    private String optionName;
    private Long additionalPrice;

    public Long getTotalPrice(){
        return (unitPrice + additionalPrice) * quantity;
    }

    public static OrderItem createOrderItem(Order order, CartItem cartItem){

        return OrderItem.builder()
                .order(order)
                .menu(cartItem.getMenu())
                .menuName(cartItem.getMenu().getName())
                .description(cartItem.getMenu().getDescription())
                .unitPrice(cartItem.getMenu().getPrice())
                .quantity(cartItem.getQuantity())
                .optionName(cartItem.getMenuOption().getName())
                .additionalPrice(cartItem.getMenuOption().getAdditionalPrice())
                .build();
    }
}
