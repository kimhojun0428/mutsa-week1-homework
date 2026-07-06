package mutsa.delivery.controller;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.controller.docs.ShopApiDocs;
import mutsa.delivery.dto.shop.ShopResponseDto;
import mutsa.delivery.global.apiPayload.GlobalResponse;
import mutsa.delivery.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController implements ShopApiDocs {

    private final ShopService shopService;

    @Override
    @GetMapping
    public ResponseEntity<GlobalResponse<List<ShopResponseDto>>> getShops() {
        List<ShopResponseDto> responseDto = shopService.getShops();

        return ResponseEntity.ok(GlobalResponse.onSuccess(responseDto));
    }
}
