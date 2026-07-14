package mutsa.delivery.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import mutsa.delivery.global.apiPayload.GlobalResponse;

@Tag(name = "Health API", description = "서버 상태 확인 API")
public interface HealthApiDocs {

    @Operation(
            summary = "서버 상태 확인",
            description = "애플리케이션이 정상적으로 요청을 처리할 수 있는지 확인합니다."
    )
    @SecurityRequirements
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "서버 정상(COMMON_200_1)")
    })
    GlobalResponse<String> health();
}
