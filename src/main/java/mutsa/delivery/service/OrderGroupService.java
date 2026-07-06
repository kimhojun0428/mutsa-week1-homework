package mutsa.delivery.service;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.domain.CreditHistory;
import mutsa.delivery.domain.CreditHistoryType;
import mutsa.delivery.domain.OrderGroup;
import mutsa.delivery.domain.User;
import mutsa.delivery.global.apiPayload.code.OrderGroupErrorCode;
import mutsa.delivery.global.apiPayload.exception.ProjectException;
import mutsa.delivery.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderGroupService {

    private final OrderGroupRepository orderGroupRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Transactional
    public void cancelOrderGroup(Long orderGroupId){
        OrderGroup orderGroup = orderGroupRepository.findById(orderGroupId)
                .orElseThrow(() -> new ProjectException(OrderGroupErrorCode.ORDER_GROUP_NOT_FOUND));

        orderGroup.cancelOrders();

        orderGroup.getOrders().forEach(order ->
                order.getOrderItems().forEach(orderItem -> {
                    // Menu 엔티티 내부에 정의된 재고 증가 메서드 호출
                    //orderItem.getMenu().increaseStock(orderItem.getQuantity());
                })
        );

        User user = orderGroup.getUser();
        Long refundAmount = orderGroup.getTotalPrice();
        user.chargeCredit(refundAmount);

        CreditHistory creditHistory = CreditHistory.of(user, refundAmount, CreditHistoryType.REFUND);
        creditHistoryRepository.save(creditHistory);
    }
}
