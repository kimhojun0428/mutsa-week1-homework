package mutsa.delivery.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mutsa.delivery.global.apiPayload.code.UserErrorCode;
import mutsa.delivery.global.apiPayload.exception.ProjectException;

@Entity
@Table(name = "cart_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // NOTE: Menu·MenuOption은 팀원(메뉴 도메인) 담당 엔티티입니다. INTEGRATION.md 참고.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_option_id", nullable = false)
    private MenuOption menuOption;

    @Builder(access = AccessLevel.PRIVATE)
    private CartItem(Cart cart, Menu menu, MenuOption menuOption, int quantity) {
        this.cart = cart;
        this.menu = menu;
        this.menuOption = menuOption;
        this.quantity = quantity;
    }

    public static CartItem create(Cart cart, Menu menu, MenuOption menuOption, int quantity) {
        CartItem newCartItem = CartItem.builder()
                .cart(cart)
                .menu(menu)
                .menuOption(menuOption)
                .quantity(quantity)
                .build();

        cart.addCartItem(newCartItem);

        return newCartItem;
    }

    public void validateUser(Long userId) {
        if (!this.cart.getUser().getId().equals(userId)) {
            throw new ProjectException(UserErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    public void increaseQuantity(int quantity) {
        this.quantity += quantity;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    /** 단가 = 메뉴 가격 + 옵션 추가금액, 소계 = 단가 * 수량 */
    public long getUnitPrice() {
        return this.menu.getPrice() + this.menuOption.getAdditionalPrice();
    }

    public long calculatePrice() {
        return getUnitPrice() * this.quantity;
    }
}
