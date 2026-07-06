package mutsa.delivery.repository;

import mutsa.delivery.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query("""
            select distinct m
            from Menu m
            left join fetch m.options
            where m.shop.id = :shopId
            order by m.id asc
            """)
    List<Menu> findAllWithOptionsByShopId(@Param("shopId") Long shopId);
}
