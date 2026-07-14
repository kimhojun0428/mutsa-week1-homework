package mutsa.delivery.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import mutsa.delivery.dto.shop.ShopResponseDto;
import mutsa.delivery.global.apiPayload.GlobalResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Shop API", description = "상점 도메인 API")
public interface ShopApiDocs {

    @Operation(summary = "상점 조회", description = "등록된 상점 목록을 조회합니다.")
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상점 조회 성공(COMMON_200_1)")
    })
    ResponseEntity<GlobalResponse<List<ShopResponseDto>>> getShops();
}
