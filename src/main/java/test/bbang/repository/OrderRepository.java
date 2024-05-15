package test.bbang.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import test.bbang.Entity.Customer;
import test.bbang.Entity.Order;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByCustomer(Customer customer);

    @Query("SELECT o FROM Order o WHERE o.orderDate >= :startDate AND o.orderDate <= :endDate")
    List<Order> findAllWithOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // 주문을 날짜 순으로 오름차순 정렬하여 조회
    List<Order> findAllByOrderByOrderDateAsc();
}
