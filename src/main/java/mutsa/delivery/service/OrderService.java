package mutsa.delivery.service;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.domain.Address;
import mutsa.delivery.domain.Cart;
import mutsa.delivery.domain.Order;
import mutsa.delivery.domain.User;
import mutsa.delivery.dto.order.OrderRequestDto;
import mutsa.delivery.global.apiPayload.code.AddressErrorCode;
import mutsa.delivery.global.apiPayload.code.CartErrorCode;
import mutsa.delivery.global.apiPayload.code.OrderErrorCode;
import mutsa.delivery.global.apiPayload.code.UserErrorCode;
import mutsa.delivery.global.apiPayload.exception.ProjectException;
import mutsa.delivery.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderGroupRepository orderGroupRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final MenuRepository menuRepository;

    public Long createOrder(Long userId, OrderRequestDto orderRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ProjectException(UserErrorCode.USER_NOT_FOUND));

        Address address = addressRepository.findById(orderRequestDto.getAddressId())
                .orElseThrow(() -> new ProjectException(AddressErrorCode.ADDRESS_NOT_FOUND));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(()-> new ProjectException(CartErrorCode.CART_NOT_FOUND));


    }

    @Transactional
    public void cancleOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new ProjectException(OrderErrorCode.ORDER_NOT_FOUND));

        order.cancelOrder();
    }

}
