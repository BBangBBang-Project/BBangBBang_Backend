package test.bbang.Dto.Cart;

import lombok.Data;
import test.bbang.Dto.Order.OrderItemDto;
import java.util.List;

// 장바구니 상품 구매 응답 DTO
@Data
public class PurchaseResponseDto {
    private String status; // 예: "성공", "실패"
    private String message; // 예: "구매가 완료되었습니다."
    private List<OrderItemDto> orderItems; // 구매 성공 시 주문 항목 목록
}
