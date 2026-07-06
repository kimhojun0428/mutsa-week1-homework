package mutsa.delivery.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    private String name;
    private String description;
    private Long price;
    private Integer stock;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<MenuOption> options = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Menu(
            Shop shop,
            String name,
            String description,
            Long price,
            Integer stock
    ) {
        this.shop = shop;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public static Menu create(
            Shop shop,
            String name,
            String description,
            Long price,
            Integer stock
    ) {
        Menu menu = Menu.builder()
                .shop(shop)
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .build();
        shop.addMenu(menu);
        return menu;
    }

    public void addOption(MenuOption option) {
        this.options.add(option);
    }
}
