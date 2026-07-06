package mutsa.delivery.dto.address;

import mutsa.delivery.domain.Address;

public record AddressResponseDto(
        Long addressId,
        String addressName,
        String address,
        String zipCode,
        String phoneNumber
) {
    public static AddressResponseDto from(Address address) {
        return new AddressResponseDto(
                address.getId(),
                address.getAddressName(),
                address.getAddress(),
                address.getZipCode(),
                address.getPhoneNumber()
        );
    }
}
