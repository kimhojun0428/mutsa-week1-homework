package mutsa.delivery.service;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.domain.Address;
import mutsa.delivery.domain.User;
import mutsa.delivery.dto.address.AddAddressRequestDto;
import mutsa.delivery.dto.address.AddressResponseDto;
import mutsa.delivery.dto.address.UpdateAddressRequestDto;
import mutsa.delivery.global.apiPayload.code.AddressErrorCode;
import mutsa.delivery.global.apiPayload.code.UserErrorCode;
import mutsa.delivery.global.apiPayload.exception.ProjectException;
import mutsa.delivery.repository.AddressRepository;
import mutsa.delivery.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public AddressResponseDto addAddress(Long userId, AddAddressRequestDto requestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ProjectException(UserErrorCode.USER_NOT_FOUND));

        if (addressRepository.existsByUser_IdAndAddressName(userId, requestDto.addressName())) {
            throw new ProjectException(AddressErrorCode.ADDRESS_ALREADY_EXISTS);
        }

        Address newAddress = Address.create(
                user,
                requestDto.addressName(),
                requestDto.address(),
                requestDto.zipCode(),
                requestDto.phoneNumber()
        );
        addressRepository.save(newAddress);

        return AddressResponseDto.from(newAddress);
    }

    public AddressResponseDto getAddress(Long userId, Long addressId) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ProjectException(AddressErrorCode.ADDRESS_NOT_FOUND));

        address.validateUser(userId);

        return AddressResponseDto.from(address);
    }

    public List<AddressResponseDto> getAllAddress(Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new ProjectException(UserErrorCode.USER_NOT_FOUND));

        return addressRepository.findAllByUser_Id(userId).stream()
                .map(AddressResponseDto::from)
                .toList();
    }

    @Transactional
    public AddressResponseDto updateAddress(Long userId, Long addressId, UpdateAddressRequestDto requestDto) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ProjectException(AddressErrorCode.ADDRESS_NOT_FOUND));

        address.validateUser(userId);

        if (!address.getAddressName().equals(requestDto.addressName())) {
            if (addressRepository.existsByUser_IdAndAddressName(userId, requestDto.addressName())) {
                throw new ProjectException(AddressErrorCode.ADDRESS_NAME_ALREADY_EXISTS);
            }
        }

        address.updateAddress(
                requestDto.addressName(),
                requestDto.address(),
                requestDto.zipCode(),
                requestDto.phoneNumber()
        );

        return AddressResponseDto.from(address);
    }

    @Transactional
    public void deleteAddress(Long userId, Long addressId) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ProjectException(AddressErrorCode.ADDRESS_NOT_FOUND));

        address.validateUser(userId);

        addressRepository.delete(address);
    }
}
