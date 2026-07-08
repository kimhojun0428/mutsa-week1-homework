package mutsa.delivery.service; // 💡 서비스 테스트이므로 service 패키지 아래에 위치시킵니다.

import mutsa.delivery.domain.Address;
import mutsa.delivery.domain.Cart;
import mutsa.delivery.domain.User;
import mutsa.delivery.domain.OrderGroup;
import mutsa.delivery.dto.OrderGroupRequestDto;
import mutsa.delivery.dto.OrderGroupResponseDto;
import mutsa.delivery.repository.AddressRepository;
import mutsa.delivery.repository.CartRepository;
import mutsa.delivery.repository.OrderGroupRepository;
import mutsa.delivery.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // 💡 테스트가 끝나면 DB에 쌓인 가짜 데이터를 자동으로 Rollback(지우기) 해줍니다.
class OrderGroupServiceTest {

    @Autowired private OrderGroupService orderGroupService;
    @Autowired private UserRepository userRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private OrderGroupRepository orderGroupRepository;

    @Test
    @DisplayName("장바구니 물품들로 주문 그룹 생성 및 결제가 정상 동작한다")
    void createOrderGroupSuccessTest() {
        // ==================== [1. Given: 테스트 환경 구성] ====================

        // 1-1. 테스트용 유저 생성 (돈을 넉넉히 50,000C 쥐어줍니다)
        User user = User.builder()
                .name("김팀장")
                .credit(50000L)
                .build();
        userRepository.save(user);

        // 1-2. 테스트용 배송지 생성
        Address address = Address.builder()
                .user(user)
                .address("서울시 마포구 와우산로")
                .name("홍익대학교")
                .phone("010-1234-5678")
                .build();
        addressRepository.save(address);

        // 1-3. 테스트용 장바구니 생성
        Cart cart = Cart.builder()
                .user(user)
                .build();
        cartRepository.save(cart);

        // 💡 [참고] 원래는 여기에 가짜 Menu와 가짜 CartItem을 만들어서
        // cart.getCartItems().add(item) 등으로 장바구니를 채우는 코드가 들어가야 합니다.
        // 현재는 빈 장바구니 상태이므로, 팀원 A가 만든 CartItem 빌더 형식에 맞춰 채워주셔야 합니다.

        // 1-4. 컨트롤러가 던져줄 DTO 가짜 요청 만들기
        OrderGroupRequestDto requestDto = new OrderGroupRequestDto(address.getId());


        // ==================== [2. When: 실제 로직 실행] ====================

        OrderGroupResponseDto response = orderGroupService.createOrderGroup(user.getId(), requestDto);


        // ==================== [3. Then: 결과 검증 (정합성 체크)] ====================

        // 검증 1: 결과로 리턴된 주문그룹 ID가 정상적으로 발급되었는가?
        assertThat(response.orderGroupId()).isNotNull();

        // 검증 2: 데이터베이스에 실제로 OrderGroup이 저장되었는가?
        OrderGroup savedGroup = orderGroupRepository.findById(response.orderGroupId()).orElseThrow();
        assertThat(savedGroup.getUser().getId()).isEqualTo(user.getId());

        // 검증 3: 유저의 잔여 크레딧이 정상적으로 차감되었는가?
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        System.out.println("====== [테스트 결과 확인] ======");
        System.out.println("주문 전 크레딧: 50,000C");
        System.out.println("주문 후 남은 크레딧: " + updatedUser.getCredit() + "C");
        System.out.println("주문 총 금액: " + savedGroup.getTotalPrice() + "C");
        System.out.println("=============================");
    }
}