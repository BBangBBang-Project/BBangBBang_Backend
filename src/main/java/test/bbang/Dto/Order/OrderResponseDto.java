package test.bbang.Dto.Order;

import lombok.Data;
import test.bbang.Entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// 고객의 주문 내역에 대한 응답 DTO
@Data
public class OrderResponseDto {
    private Long orderId;
    private List<OrderItemDto> orderItems;
    private Double totalAmount;
    private LocalDateTime orderDate;

    public OrderResponseDto(Order order) {
        this.orderId = order.getOrderId();
        this.orderItems = order.getOrderItems().stream()
                .map(OrderItemDto::new)
                .collect(Collectors.toList());
        this.totalAmount = order.getTotalPrice();
        this.orderDate = order.getOrderDate();
    }
}
