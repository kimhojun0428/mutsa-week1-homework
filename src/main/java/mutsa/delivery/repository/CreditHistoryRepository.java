package mutsa.delivery.repository;

import mutsa.delivery.domain.CreditHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditHistoryRepository extends JpaRepository<CreditHistory, Long> {

    List<CreditHistory> findAllByUser_IdOrderByIdDesc(Long userId);
}
