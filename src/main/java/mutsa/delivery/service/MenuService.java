package mutsa.delivery.service;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.dto.menu.MenuResponseDto;
import mutsa.delivery.global.apiPayload.code.ShopErrorCode;
import mutsa.delivery.global.apiPayload.exception.ProjectException;
import mutsa.delivery.repository.MenuRepository;
import mutsa.delivery.repository.ShopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final ShopRepository shopRepository;

    public List<MenuResponseDto> getMenus(Long shopId) {
        if (!shopRepository.existsById(shopId)) {
            throw new ProjectException(ShopErrorCode.SHOP_NOT_FOUND);
        }

        return menuRepository.findAllWithOptionsByShopId(shopId).stream()
                .map(MenuResponseDto::from)
                .toList();
    }
}
