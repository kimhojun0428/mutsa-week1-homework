package mutsa.delivery.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mutsa.delivery.dto.address.AddAddressRequestDto;
import mutsa.delivery.dto.address.AddressResponseDto;
import mutsa.delivery.dto.address.UpdateAddressRequestDto;
import mutsa.delivery.global.apiPayload.GlobalResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Address API", description = "주소 도메인 API")
public interface AddressApiDocs {

    @Operation(summary = "주소 추가", description = "주소를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "주소 생성 성공(COMMON_201_1)"),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 조회 실패(USER_404_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "주소지 중복(ADDRESS_409_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            )
    })
    ResponseEntity<GlobalResponse<AddressResponseDto>> createAddress(Long userId, AddAddressRequestDto requestDto);

    @Operation(summary = "단일 주소 조회", description = "특정 주소의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "단일 주소 조회 성공(COMMON_200_1)"),
            @ApiResponse(
                    responseCode = "404",
                    description = "주소 조회 실패(ADDRESS_404_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 권한 없음(USER_403_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            )
    })
    ResponseEntity<GlobalResponse<AddressResponseDto>> getAddress(Long userId, Long addressId);

    @Operation(summary = "전체 주소 조회", description = "사용자의 모든 주소를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 주소 조회 성공(COMMON_200_1)"),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 조회 실패(USER_404_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            )
    })
    ResponseEntity<GlobalResponse<List<AddressResponseDto>>> getAddressList(Long userId);

    @Operation(summary = "주소 정보 변경", description = "주소지 정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주소지 변경 성공(COMMON_200_1)"),
            @ApiResponse(
                    responseCode = "404",
                    description = "주소 조회 실패(ADDRESS_404_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "주소지명 중복(ADDRESS_409_2)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            )
    })
    ResponseEntity<GlobalResponse<AddressResponseDto>> updateAddress(Long userId, Long addressId, UpdateAddressRequestDto requestDto);

    @Operation(summary = "주소 삭제", description = "저장되어 있는 주소를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주소지 삭제 성공(COMMON_200_1)"),
            @ApiResponse(
                    responseCode = "404",
                    description = "주소 조회 실패(ADDRESS_404_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "사용자 권한 없음(USER_403_1)",
                    content = @Content(schema = @Schema(implementation = GlobalResponse.class))
            )
    })
    ResponseEntity<GlobalResponse<Void>> deleteAddress(Long userId, Long addressId);
}
