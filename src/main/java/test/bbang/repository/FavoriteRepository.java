package test.bbang.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.bbang.Entity.Customer;
import test.bbang.Entity.Favorite;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByCustomer(Customer customer);
}
