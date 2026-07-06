package mutsa.delivery.init;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.domain.Shop;
import mutsa.delivery.domain.User;
import mutsa.delivery.repository.ShopRepository;
import mutsa.delivery.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DummyDataInit implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User user1 = User.create("kimmutsa@mutsa.com", "12345678", "김멋사");
            User user2 = User.create("leehongik@hongik.ac.kr", "87654321", "이홍익");
            User user3 = User.create("parkchulsu@naver.com", "abcdefgh", "박철수");
            userRepository.save(user1);
            userRepository.save(user2);
            userRepository.save(user3);

            System.out.println("[안내] 테스트용 더미 사용자 데이터가 생성되었습니다.");
        }

        if (shopRepository.count() == 0) {
            Shop shop1 = Shop.create("냠냠 분식", "02-320-0001", "T101");
            Shop shop2 = Shop.create("왕꼬치", "02-320-0002", "T102");
            shopRepository.save(shop1);
            shopRepository.save(shop2);

            System.out.println("[안내] 테스트용 더미 상점 데이터가 생성되었습니다.");
        }
    }
}
