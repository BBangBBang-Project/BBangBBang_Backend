package test.bbang.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.bbang.Entity.Customer;
import test.bbang.Entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByCustomer(Customer customer);
}
