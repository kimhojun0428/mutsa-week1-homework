package mutsa.delivery.dto.shop;

import mutsa.delivery.domain.Shop;

public record ShopResponseDto(
        Long shopId,
        String name,
        String contact,
        String location
) {
    public static ShopResponseDto from(Shop shop) {
        return new ShopResponseDto(
                shop.getId(),
                shop.getName(),
                shop.getContact(),
                shop.getLocation()
        );
    }
}
