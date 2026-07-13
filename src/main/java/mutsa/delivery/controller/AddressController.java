package mutsa.delivery.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mutsa.delivery.controller.docs.AddressApiDocs;
import mutsa.delivery.dto.address.AddAddressRequestDto;
import mutsa.delivery.dto.address.AddressResponseDto;
import mutsa.delivery.dto.address.UpdateAddressRequestDto;
import mutsa.delivery.global.apiPayload.GlobalResponse;
import mutsa.delivery.global.config.security.AuthUser;
import mutsa.delivery.service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController implements AddressApiDocs {

    private final AddressService addressService;

    @Override
    @PostMapping
    public ResponseEntity<GlobalResponse<AddressResponseDto>> createAddress(
            @AuthUser Long userId,
            @Valid
            @RequestBody AddAddressRequestDto requestDto
    ) {
        AddressResponseDto responseDto = addressService.addAddress(userId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalResponse.onSuccessCreate(responseDto));
    }

    @Override
    @GetMapping("/{addressId}")
    public ResponseEntity<GlobalResponse<AddressResponseDto>> getAddress(
            @AuthUser Long userId,
            @PathVariable("addressId") Long addressId
    ) {
        AddressResponseDto responseDto = addressService.getAddress(userId, addressId);

        return ResponseEntity.ok(GlobalResponse.onSuccess(responseDto));
    }

    @Override
    @GetMapping
    public ResponseEntity<GlobalResponse<List<AddressResponseDto>>> getAddressList(
            @AuthUser Long userId
    ) {
        List<AddressResponseDto> responseDto = addressService.getAllAddress(userId);

        return ResponseEntity.ok(GlobalResponse.onSuccess(responseDto));
    }

    @Override
    @PutMapping("/{addressId}")
    public ResponseEntity<GlobalResponse<AddressResponseDto>> updateAddress(
            @AuthUser Long userId,
            @PathVariable("addressId") Long addressId,
            @Valid
            @RequestBody UpdateAddressRequestDto requestDto
    ) {
        AddressResponseDto responseDto = addressService.updateAddress(userId, addressId, requestDto);

        return ResponseEntity.ok(GlobalResponse.onSuccess(responseDto));
    }

    @Override
    @DeleteMapping("/{addressId}")
    public ResponseEntity<GlobalResponse<Void>> deleteAddress(
            @AuthUser Long userId,
            @PathVariable("addressId") Long addressId
    ) {
        addressService.deleteAddress(userId, addressId);

        return ResponseEntity.ok(GlobalResponse.onSuccess());
    }
}
