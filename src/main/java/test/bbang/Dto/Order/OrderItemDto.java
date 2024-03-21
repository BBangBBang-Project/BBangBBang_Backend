package test.bbang.Dto.Order;

import lombok.Data;
import test.bbang.Entity.OrderItem;

import java.time.LocalDateTime;

// 주문 항목 DTO
@Data
public class OrderItemDto {
    private Long productId;
    private String productName;
    private Double price;
    private Integer quantity;
    //private String imageUrl;

    public OrderItemDto(OrderItem orderItem) {
        this.productId = orderItem.getBread().getId(); // 빵의 ID
        this.productName = orderItem.getBread().getName(); // 빵의 이름
        this.price = orderItem.getPrice(); // 가격
        this.quantity = orderItem.getQuantity(); // 수량
    }
}

