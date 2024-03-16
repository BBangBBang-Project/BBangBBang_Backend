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
    private String imageUrl;
    private LocalDateTime purchaseDate;

    public OrderItemDto(OrderItem orderItem) {
        // OrderItem 객체로부터 필요한 정보를 매핑합니다.
    }
}

