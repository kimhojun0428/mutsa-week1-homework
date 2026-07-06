package mutsa.delivery.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddCartItemRequestDto(

        @NotNull(message = "메뉴 ID는 필수입니다.")
        Long menuId,

        @NotNull(message = "메뉴 옵션 ID는 필수입니다.")
        Long menuOptionId,

        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        int quantity

) {}
