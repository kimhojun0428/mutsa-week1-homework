package mutsa.delivery.dto.menu;

import mutsa.delivery.domain.MenuOption;

public record MenuOptionResponseDto(
        Long menuOptionId,
        String name,
        Long additionalPrice
) {
    public static MenuOptionResponseDto from(MenuOption option) {
        return new MenuOptionResponseDto(
                option.getId(),
                option.getName(),
                option.getAdditionalPrice()
        );
    }
}
