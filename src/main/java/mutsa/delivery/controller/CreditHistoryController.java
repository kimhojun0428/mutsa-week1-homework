package mutsa.delivery.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mutsa.delivery.controller.docs.CreditApiDocs;
import mutsa.delivery.dto.credit.CreditChangeRequestDto;
import mutsa.delivery.dto.credit.CreditHistoryResponseDto;
import mutsa.delivery.global.apiPayload.GlobalResponse;
import mutsa.delivery.global.config.security.AuthUser;
import mutsa.delivery.service.CreditHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credits")
@RequiredArgsConstructor
public class CreditHistoryController implements CreditApiDocs {

    private final CreditHistoryService creditHistoryService;

    @Override
    @PostMapping
    public ResponseEntity<GlobalResponse<CreditHistoryResponseDto>> changeCredit(
            @AuthUser Long userId,
            @Valid
            @RequestBody CreditChangeRequestDto requestDto
    ) {
        CreditHistoryResponseDto responseDto = creditHistoryService.changeCredit(userId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponse.onSuccessCreate(responseDto));
    }

    @Override
    @GetMapping
    public ResponseEntity<GlobalResponse<List<CreditHistoryResponseDto>>> getHistories(
            @AuthUser Long userId
    ) {
        List<CreditHistoryResponseDto> responseDto = creditHistoryService.getHistories(userId);

        return ResponseEntity.ok(GlobalResponse.onSuccess(responseDto));
    }
}
