package mutsa.delivery.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menu_options")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    private String name;
    private Long additionalPrice;

    @Builder(access = AccessLevel.PRIVATE)
    private MenuOption(Menu menu, String name, Long additionalPrice) {
        this.menu = menu;
        this.name = name;
        this.additionalPrice = additionalPrice;
    }

    public static MenuOption create(Menu menu, String name, Long additionalPrice) {
        MenuOption option = MenuOption.builder()
                .menu(menu)
                .name(name)
                .additionalPrice(additionalPrice)
                .build();
        menu.addOption(option);
        return option;
    }
}
