package mutsa.delivery.dto.shop;

import mutsa.delivery.domain.Shop;
import mutsa.delivery.domain.ShopCategory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ShopResponseDtoTest {

    @Test
    void mapsImageUrlForDummyShops() {
        Shop nyamnyam = Shop.create("냠냠 분식", "02-320-0001", "T101", ShopCategory.SNACK);
        Shop wangkkochi = Shop.create("왕꼬치", "02-320-0002", "T102", ShopCategory.ETC);

        assertThat(ShopResponseDto.from(nyamnyam).imageUrl())
                .isEqualTo("https://week7-homework.vercel.app/nyamnyam.jpeg");
        assertThat(ShopResponseDto.from(wangkkochi).imageUrl())
                .isEqualTo("https://week7-homework.vercel.app/wangkkochi.jpeg");
    }

    @Test
    void returnsNullImageUrlForUnknownShop() {
        Shop shop = Shop.create("새 상점", "02-0000-0000", "T999", ShopCategory.ETC);

        assertThat(ShopResponseDto.from(shop).imageUrl()).isNull();
    }
}
