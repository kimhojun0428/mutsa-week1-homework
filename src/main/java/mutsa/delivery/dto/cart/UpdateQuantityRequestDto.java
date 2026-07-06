package mutsa.delivery.dto.cart;

import jakarta.validation.constraints.Min;

public record UpdateQuantityRequestDto(

        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        int quantity

) {}
