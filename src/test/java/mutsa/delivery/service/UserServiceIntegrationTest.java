package mutsa.delivery.service;

import mutsa.delivery.domain.Address;
import mutsa.delivery.domain.Cart;
import mutsa.delivery.domain.CartItem;
import mutsa.delivery.domain.CreditHistory;
import mutsa.delivery.domain.CreditHistoryType;
import mutsa.delivery.domain.Menu;
import mutsa.delivery.domain.MenuOption;
import mutsa.delivery.domain.OrderGroup;
import mutsa.delivery.domain.User;
import mutsa.delivery.repository.AddressRepository;
import mutsa.delivery.repository.CartItemRepository;
import mutsa.delivery.repository.CartRepository;
import mutsa.delivery.repository.CreditHistoryRepository;
import mutsa.delivery.repository.MenuRepository;
import mutsa.delivery.repository.OrderGroupRepository;
import mutsa.delivery.repository.OrderRepository;
import mutsa.delivery.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private CreditHistoryRepository creditHistoryRepository;
    @Autowired private MenuRepository menuRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderGroupRepository orderGroupRepository;

    @Test
    void deleteUserRemovesAllOwnedData() {
        User user = userRepository.save(User.create("delete-test@example.com", "password", "tester"));
        Address address = addressRepository.save(Address.create(
                user, "home", "Seoul", "00000", "010-0000-0000"));
        creditHistoryRepository.save(CreditHistory.of(user, 1_000L, CreditHistoryType.CHARGE));

        Menu menu = menuRepository.findAll().get(0);
        MenuOption option = menu.getOptions().get(0);
        Cart cart = cartRepository.save(Cart.create(user));
        CartItem cartItem = cartItemRepository.save(CartItem.create(cart, menu, option, 1));
        OrderGroup orderGroup = orderGroupRepository.save(
                OrderGroup.createOrderGroup(user, address, List.of(cartItem)));

        Long userId = user.getId();
        Long addressId = address.getId();
        Long cartId = cart.getId();
        Long orderGroupId = orderGroup.getId();

        userService.deleteUser(userId);
        userRepository.flush();

        assertThat(userRepository.findById(userId)).isEmpty();
        assertThat(addressRepository.findById(addressId)).isEmpty();
        assertThat(cartRepository.findById(cartId)).isEmpty();
        assertThat(creditHistoryRepository.findAllByUser_IdOrderByIdDesc(userId)).isEmpty();
        assertThat(orderRepository.findAllByUser_Id(userId)).isEmpty();
        assertThat(orderGroupRepository.findById(orderGroupId)).isEmpty();
    }
}
