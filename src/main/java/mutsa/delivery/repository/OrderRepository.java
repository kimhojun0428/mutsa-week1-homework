package mutsa.delivery.repository;

import mutsa.delivery.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByUser_Id(Long userId);
}
