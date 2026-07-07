package mutsa.delivery.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mutsa.delivery.global.common.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Cart(User user) {
        this.user = user;
    }

    public static Cart create(User user) {
        return Cart.builder()
                .user(user)
                .build();
    }

    public void addCartItem(CartItem cartItem) {
        this.cartItems.add(cartItem);
    }

    public long getTotalPrice() {
        return cartItems.stream()
                .mapToLong(CartItem::calculatePrice)
                .sum();
    }

    public int getTotalQuantity() {
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}
