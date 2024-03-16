package test.bbang.Dto.Cart;

import lombok.Data;

// 장바구니에 상품을 추가하기 위한 요청 DTO
@Data
public class CartItemRequestDto {
    private Long breadId;
    private Integer quantity;
}
