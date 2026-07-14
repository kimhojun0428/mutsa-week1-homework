package mutsa.delivery.repository;

import mutsa.delivery.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findAllByUser_Id(Long userId);

    boolean existsByUser_IdAndAddressName(Long userId, String addressName);

    long deleteAllByUser_Id(Long userId);
}
