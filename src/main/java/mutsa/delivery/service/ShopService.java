package mutsa.delivery.service;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.dto.shop.ShopResponseDto;
import mutsa.delivery.repository.ShopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;

    public List<ShopResponseDto> getShops() {
        return shopRepository.findAllByOrderByIdAsc().stream()
                .map(ShopResponseDto::from)
                .toList();
    }
}
