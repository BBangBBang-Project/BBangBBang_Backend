package test.bbang.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.bbang.Entity.Bread;
import test.bbang.Entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {
    Optional<Order> findByUserId(Long userId);
}
