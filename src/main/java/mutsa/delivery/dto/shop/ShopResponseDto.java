package mutsa.delivery.dto.shop;

import mutsa.delivery.domain.Shop;
import mutsa.delivery.domain.ShopCategory;

public record ShopResponseDto(
        Long shopId,
        String name,
        String imageUrl,
        String contact,
        String location,
        ShopCategory category
) {
    public static ShopResponseDto from(Shop shop) {
        return new ShopResponseDto(
                shop.getId(),
                shop.getName(),
                resolveImageUrl(shop.getName()),
                shop.getContact(),
                shop.getLocation(),
                shop.getCategory()
        );
    }

    private static String resolveImageUrl(String shopName) {
        return switch (shopName) {
            case "냠냠 분식" -> "https://week7-homework.vercel.app/nyamnyam.jpeg";
            case "왕꼬치" -> "https://week7-homework.vercel.app/wangkkochi.jpeg";
            default -> null;
        };
    }
}
