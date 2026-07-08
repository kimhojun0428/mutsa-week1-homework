package mutsa.delivery.dto.shop;

import mutsa.delivery.domain.Shop;
import mutsa.delivery.domain.ShopCategory;

public record ShopResponseDto(
        Long shopId,
        String name,
        String contact,
        String location,
        ShopCategory category
) {
    public static ShopResponseDto from(Shop shop) {
        return new ShopResponseDto(
                shop.getId(),
                shop.getName(),
                shop.getContact(),
                shop.getLocation(),
                shop.getCategory()
        );
    }
}
