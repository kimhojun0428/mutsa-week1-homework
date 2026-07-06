package mutsa.delivery.init;

import lombok.RequiredArgsConstructor;
import mutsa.delivery.domain.User;
import mutsa.delivery.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DummyDataInit implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return;
        }

        User user1 = User.create("kimmutsa@mutsa.com", "12345678", "김멋사");
        User user2 = User.create("leehongik@hongik.ac.kr", "87654321", "이홍익");
        User user3 = User.create("parkchulsu@naver.com", "abcdefgh", "박철수");
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        System.out.println("[안내] 테스트용 더미 사용자 데이터가 생성되었습니다.");
    }
}
