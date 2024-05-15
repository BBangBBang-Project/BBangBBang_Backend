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
    public OrderItemDto(OrderItem orderItem) {
        this.productId = orderItem.getBread().getId(); // 빵의 ID
        this.productName = orderItem.getBread().getName(); // 빵의 이름
        this.price = orderItem.getPrice(); // 가격
        this.quantity = orderItem.getQuantity(); // 수량
        this.imageUrl = getImageUrl(orderItem.getBread().getImagePath());
    }

    public String getImageUrl(String imagePath) {
        // 웹 서버의 도메인 또는 IP 주소
        String baseUrl = "http://52.79.172.135:8080";

        // imagePath가 null이 아닐 때만 처리
        if (imagePath != null && !imagePath.isEmpty()) {
            // 이미지 경로에서 'uploads' 디렉토리까지의 경로를 제거하고, URL 생성
            String imageUrl = baseUrl + "/images/" + imagePath.substring(imagePath.lastIndexOf("uploads") + 8);
            return imageUrl;
        } else {
            // imagePath가 null이거나 빈 문자열일 경우 기본 이미지 URL을 반환하거나, null을 반환
            // 예: 기본 이미지를 가리키는 URL이나, null을 반환하거나, 혹은 적절한 처리를 할 수 있습니다.
            return baseUrl + "/images/default-image.png"; // 예시로 기본 이미지 경로를 설정
        }
    }
}

