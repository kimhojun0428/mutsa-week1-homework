package mutsa.delivery.dto.menu;

import mutsa.delivery.domain.Menu;

import java.util.List;

public record MenuResponseDto(
        Long menuId,
        Long shopId,
        String name,
        String description,
        Long price,
        Integer stock,
        List<MenuOptionResponseDto> options
) {
    public static MenuResponseDto from(Menu menu) {
        return new MenuResponseDto(
                menu.getId(),
                menu.getShop().getId(),
                menu.getName(),
                menu.getDescription(),
                menu.getPrice(),
                menu.getStock(),
                menu.getOptions().stream()
                        .map(MenuOptionResponseDto::from)
                        .toList()
        );
    }
}
