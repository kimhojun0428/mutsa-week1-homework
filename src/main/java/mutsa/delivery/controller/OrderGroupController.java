package mutsa.delivery.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mutsa.delivery.controller.docs.OrderGroupApiDocs;
import mutsa.delivery.dto.orderGroup.OrderGroupDetailResponseDto;
import mutsa.delivery.dto.orderGroup.OrderGroupRequestDto;
import mutsa.delivery.dto.orderGroup.OrderGroupResponseDto;
import mutsa.delivery.global.apiPayload.GlobalResponse;
import mutsa.delivery.global.config.security.AuthUser;
import mutsa.delivery.service.OrderGroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderGroupController implements OrderGroupApiDocs {

    private final OrderGroupService orderGroupService;

    @Override
    @PostMapping
    public ResponseEntity<GlobalResponse<OrderGroupResponseDto>> createOrderGroup(
            @AuthUser Long userId,
            @Valid @RequestBody OrderGroupRequestDto requestDto
    ) {
        OrderGroupResponseDto responseDto = orderGroupService.createOrderGroup(userId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponse.onSuccessCreate(responseDto));
    }

    @Override
    @PatchMapping("/{orderGroupId}")
    public ResponseEntity<GlobalResponse<Void>> cancelOrderGroup(
            @AuthUser Long userId,
            @PathVariable("orderGroupId") Long orderGroupId
    ) {
        orderGroupService.cancelOrderGroup(orderGroupId, userId);
        return ResponseEntity.ok(GlobalResponse.onSuccess());
    }

    @Override
    @GetMapping("/{orderGroupId}")
    public ResponseEntity<GlobalResponse<OrderGroupDetailResponseDto>> getOrderGroupDetail(
            @AuthUser Long userId,
            @PathVariable("orderGroupId") Long orderGroupId
    ) {
        OrderGroupDetailResponseDto responseDto = orderGroupService.getOrderGroupDetail(orderGroupId, userId);

        return ResponseEntity.ok(GlobalResponse.onSuccess(responseDto));
    }
}