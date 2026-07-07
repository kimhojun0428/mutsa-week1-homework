package mutsa.delivery.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface OrderGroupRepository extends JpaRepository<OrderGroup, Long> {

    List<OrderGroup> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("select og from OrderGroup og " +
            "join fetch og.orders o " +
            "join fetch o.shop s " +
            "where og.id = :orderGroupId")
    Optional<OrderGroup> findDetailById(@Param("orderGroupId") Long orderGroupId);
}