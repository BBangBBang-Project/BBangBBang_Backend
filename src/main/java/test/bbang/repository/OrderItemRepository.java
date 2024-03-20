package test.bbang.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import test.bbang.Dto.Bread.SoldBreadDto;
import test.bbang.Entity.OrderItem;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder_OrderId(Long orderId);

    @Query("SELECT new test.bbang.Dto.Bread.SoldBreadDto(b.name, SUM(oi.quantity)) " +
            "FROM OrderItem oi " +
            "JOIN oi.bread b " +
            "GROUP BY b.name")
    List<SoldBreadDto> findSoldBreads();
}
