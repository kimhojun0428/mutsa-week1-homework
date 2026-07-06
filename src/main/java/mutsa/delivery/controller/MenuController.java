package mutsa.delivery.controller;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.controller.docs.MenuApiDocs;
import mutsa.delivery.dto.menu.MenuResponseDto;
import mutsa.delivery.global.apiPayload.GlobalResponse;
import mutsa.delivery.service.MenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shops/{shopId}/menus")
@RequiredArgsConstructor
public class MenuController implements MenuApiDocs {

    private final MenuService menuService;

    @Override
    @GetMapping
    public ResponseEntity<GlobalResponse<List<MenuResponseDto>>> getMenus(
            @PathVariable("shopId") Long shopId
    ) {
        List<MenuResponseDto> responseDto = menuService.getMenus(shopId);

        return ResponseEntity.ok(GlobalResponse.onSuccess(responseDto));
    }
}
