package mutsa.delivery.service;

import mutsa.delivery.domain.Cart;
import mutsa.delivery.domain.Menu;
import mutsa.delivery.domain.Shop;
import mutsa.delivery.domain.ShopCategory;
import mutsa.delivery.domain.User;
import mutsa.delivery.dto.cart.AddCartItemRequestDto;
import mutsa.delivery.global.apiPayload.code.CartErrorCode;
import mutsa.delivery.global.apiPayload.exception.ProjectException;
import mutsa.delivery.repository.CartItemRepository;
import mutsa.delivery.repository.CartRepository;
import mutsa.delivery.repository.MenuRepository;
import mutsa.delivery.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CartServiceTest {

    private final CartRepository cartRepository = mock(CartRepository.class);
    private final CartItemRepository cartItemRepository = mock(CartItemRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final MenuRepository menuRepository = mock(MenuRepository.class);
    private final CartService cartService =
            new CartService(cartRepository, cartItemRepository, userRepository, menuRepository);

    @Test
    void missingMenuReturnsCart4041() {
        User user = User.create("test@example.com", "password", "tester");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(Cart.create(user)));
        when(menuRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addMenuToCart(
                1L, new AddCartItemRequestDto(999L, 1L, 1)))
                .isInstanceOf(ProjectException.class)
                .extracting("errorCode")
                .isEqualTo(CartErrorCode.MENU_NOT_FOUND);
    }

    @Test
    void missingMenuOptionReturnsCart4042() {
        User user = User.create("test@example.com", "password", "tester");
        Menu menu = Menu.create(
                Shop.create("shop", "contact", "location", ShopCategory.ETC),
                "menu", "description", 1_000L, 10);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(Cart.create(user)));
        when(menuRepository.findById(1L)).thenReturn(Optional.of(menu));

        assertThatThrownBy(() -> cartService.addMenuToCart(
                1L, new AddCartItemRequestDto(1L, 999L, 1)))
                .isInstanceOf(ProjectException.class)
                .extracting("errorCode")
                .isEqualTo(CartErrorCode.MENU_OPTION_NOT_FOUND);
    }
}
