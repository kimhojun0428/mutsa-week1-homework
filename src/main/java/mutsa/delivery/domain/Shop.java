package mutsa.delivery.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shops")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String contact;
    private String location;

    @Enumerated(EnumType.STRING)
    private ShopCategory category;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menus = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Shop(String name, String contact, String location, ShopCategory category) {
        this.name = name;
        this.contact = contact;
        this.location = location;
        this.category = category;
    }

    public static Shop create(String name, String contact, String location, ShopCategory category) {
        return Shop.builder()
                .name(name)
                .contact(contact)
                .location(location)
                .category(category)
                .build();
    }

    public void addMenu(Menu menu) {
        this.menus.add(menu);
    }
}
