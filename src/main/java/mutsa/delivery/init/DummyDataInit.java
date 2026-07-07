package mutsa.delivery.init;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.domain.Menu;
import mutsa.delivery.domain.MenuOption;
import mutsa.delivery.domain.Shop;
import mutsa.delivery.domain.User;
import mutsa.delivery.repository.MenuRepository;
import mutsa.delivery.repository.ShopRepository;
import mutsa.delivery.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DummyDataInit implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final MenuRepository menuRepository;

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

        if (menuRepository.count() == 0) {
            List<Shop> shops = shopRepository.findAllByOrderByIdAsc();
            Shop shop1 = shops.get(0);
            Shop shop2 = shops.get(1);

            Menu tteokbokki = Menu.create(shop1, "떡볶이", "맛있는 떡볶이", 12000L, 10);
            MenuOption.create(tteokbokki, "안 맵게", 0L);
            MenuOption.create(tteokbokki, "덜 맵게", 0L);
            MenuOption.create(tteokbokki, "맵게", 0L);
            MenuOption.create(tteokbokki, "아주 맵게", 0L);

            Menu friedFood = Menu.create(shop1, "튀김", "튀김류", 12000L, 10);
            MenuOption.create(friedFood, "새우 튀김", 300L);
            MenuOption.create(friedFood, "고구마 튀김", 400L);

            Menu skewer = Menu.create(shop2, "닭꼬치", "달콤한 닭꼬치", 5000L, 20);
            MenuOption.create(skewer, "기본맛", 0L);
            MenuOption.create(skewer, "매운맛", 500L);

            menuRepository.save(tteokbokki);
            menuRepository.save(friedFood);
            menuRepository.save(skewer);

            System.out.println("[안내] 테스트용 더미 메뉴 데이터가 생성되었습니다.");
        }
    }
}
