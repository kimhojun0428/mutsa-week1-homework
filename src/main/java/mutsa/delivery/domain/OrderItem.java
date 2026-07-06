package mutsa.delivery.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.awt.*;

@Entity
@Getter
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
    private Long unitPrice;
    private Long quantity;
    private String optionName;
    private Long additionalPrice;
}
