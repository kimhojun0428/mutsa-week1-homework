package mutsa.delivery.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import mutsa.delivery.dto.menu.MenuResponseDto;
import mutsa.delivery.global.apiPayload.GlobalResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Menu API", description = "메뉴 도메인 API")
public interface MenuApiDocs {

    @Operation(summary = "상점 메뉴 목록 조회", description = "상점의 메뉴와 옵션 목록을 조회합니다.")
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상점 메뉴 목록 조회 성공(COMMON_200_1)"),
            @ApiResponse(
                    responseCode = "404",
                    description = "상점 조회 실패(SHOP_404_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            )
    })
    ResponseEntity<GlobalResponse<List<MenuResponseDto>>> getMenus(Long shopId);
}
