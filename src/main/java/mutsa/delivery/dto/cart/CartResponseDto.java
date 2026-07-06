package mutsa.delivery.dto.cart;

import mutsa.delivery.domain.Cart;

import java.util.Collections;
import java.util.List;

public record CartResponseDto(
        List<CartItemResponseDto> items,
        long totalPrice
) {
    public static CartResponseDto from(Cart cart) {
        List<CartItemResponseDto> items = cart.getCartItems().stream()
                .map(CartItemResponseDto::from)
                .toList();

        return new CartResponseDto(items, cart.getTotalPrice());
    }

    public static CartResponseDto empty() {
        return new CartResponseDto(Collections.emptyList(), 0L);
    }
}
