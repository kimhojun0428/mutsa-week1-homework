package mutsa.delivery.service;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.domain.*;
import mutsa.delivery.repository.OrderGroupRepository;
import mutsa.delivery.dto.orderGroup.OrderGroupDetailResponseDto;
import mutsa.delivery.dto.orderGroup.OrderGroupRequestDto;
import mutsa.delivery.dto.orderGroup.OrderGroupResponseDto;
import mutsa.delivery.global.apiPayload.code.*;
import mutsa.delivery.global.apiPayload.exception.ProjectException;
import mutsa.delivery.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderGroupService {

    private final OrderGroupRepository orderGroupRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Transactional
    public OrderGroupResponseDto createOrderGroup(Long userId, OrderGroupRequestDto requestDto){
        User user = userRepository.findById((userId))
                .orElseThrow(()-> new ProjectException(UserErrorCode.USER_NOT_FOUND));
        Address address = addressRepository.findById((requestDto.addressId()))
                .orElseThrow(()-> new ProjectException(AddressErrorCode.ADDRESS_NOT_FOUND));
        Cart cart = cartRepository.findByUserId((userId))
                .orElseThrow(()-> new ProjectException(CartErrorCode.CART_NOT_FOUND));
        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems.isEmpty()) {
            throw new ProjectException(CartErrorCode.CART_ITEM_NOT_FOUND);
        }
        OrderGroup orderGroup = OrderGroup.createOrderGroup(user, address, cartItems);

        // 크레딧 차감
        user.useCredit(orderGroup.getTotalPrice());

        orderGroupRepository.save(orderGroup);

        // 크레딧 히스토리 생성 및 저장
        CreditHistory creditHistory = CreditHistory.of(user, orderGroup.getTotalPrice(), CreditHistoryType.USE);
        creditHistoryRepository.save(creditHistory);

        cartService.clearCart(userId);

        return new OrderGroupResponseDto(orderGroup.getId());
    }

    public OrderGroupDetailResponseDto getOrderGroupDetail(Long orderGroupId) {

        OrderGroup orderGroup = orderGroupRepository.findDetailById(orderGroupId)
                .orElseThrow(() -> new ProjectException(OrderErrorCode.ORDER_NOT_FOUND));

        List<OrderGroupDetailResponseDto.OrderDetailDto> orderDetailDtos = orderGroup.getOrders().stream()
                .map(order -> {


                    List<OrderGroupDetailResponseDto.OrderItemDto> itemDtos = order.getOrderItems().stream()
                            .map(item -> new OrderGroupDetailResponseDto.OrderItemDto(
                                    item.getMenuName(),
                                    item.getUnitPrice(),
                                    item.getQuantity(),
                                    item.getOptionName(),
                                    item.getAdditionalPrice()
                            ))
                            .toList();

                    return new OrderGroupDetailResponseDto.OrderDetailDto(
                            order.getId(),
                            order.getShop().getName(),
                            order.getOrderStatus().name(),
                            itemDtos
                    );
                })
                .toList();

        return new OrderGroupDetailResponseDto(
                orderGroup.getId(),
                orderGroup.getTotalPrice(),
                orderGroup.getAddress(),
                orderGroup.getAddressName(),
                orderGroup.getPhone(),
                orderGroup.getCreatedAt(),
                orderDetailDtos
        );
    }

    @Transactional
    public void cancelOrderGroup(Long orderGroupId){
        OrderGroup orderGroup = orderGroupRepository.findById(orderGroupId)
                .orElseThrow(() -> new ProjectException(OrderGroupErrorCode.ORDER_GROUP_NOT_FOUND));

        orderGroup.cancelOrders();

        orderGroup.getOrders().forEach(order ->
                order.getOrderItems().forEach(orderItem -> {
                    orderItem.getMenu().increaseStock(orderItem.getQuantity());
                })
        );

        User user = orderGroup.getUser();
        Long refundAmount = orderGroup.getTotalPrice();
        user.chargeCredit(refundAmount);

        CreditHistory creditHistory = CreditHistory.of(user, refundAmount, CreditHistoryType.REFUND);
        creditHistoryRepository.save(creditHistory);
    }
}
