package mutsa.delivery.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mutsa.delivery.dto.credit.CreditChangeRequestDto;
import mutsa.delivery.dto.credit.CreditHistoryResponseDto;
import mutsa.delivery.global.apiPayload.GlobalResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Credit API", description = "크레딧 도메인 API")
public interface CreditApiDocs {

    @Operation(summary = "크레딧 변동", description = "type(CHARGE/USE/REFUND)에 따라 크레딧을 증감하고 기록을 남깁니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "크레딧 변동 성공(COMMON_201_1)"),
            @ApiResponse(
                    responseCode = "400",
                    description = "잔액 부족(CREDIT_400_1) / 잘못된 요청",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 조회 실패(USER_404_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            )
    })
    ResponseEntity<GlobalResponse<CreditHistoryResponseDto>> changeCredit(Long userId, CreditChangeRequestDto requestDto);

    @Operation(summary = "크레딧 내역 조회", description = "사용자의 크레딧 변동 내역을 최신순으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "크레딧 내역 조회 성공(COMMON_200_1)"),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 조회 실패(USER_404_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            )
    })
    ResponseEntity<GlobalResponse<List<CreditHistoryResponseDto>>> getHistories(Long userId);
}
