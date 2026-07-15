package mutsa.delivery.dto.cart;

import mutsa.delivery.domain.CartItem;

public record CartItemResponseDto(
        Long cartItemId,
        Long menuId,
        Long menuOptionId,
        String name,
        String optionName,
        long price,
        int quantity
) {
    public static CartItemResponseDto from(CartItem cartItem) {
        return new CartItemResponseDto(
                cartItem.getId(),
                cartItem.getMenu().getId(),
                cartItem.getMenuOption().getId(),
                cartItem.getMenu().getName(),
                cartItem.getMenuOption().getName(),
                cartItem.getUnitPrice(),
                cartItem.getQuantity()
        );
    }
}
