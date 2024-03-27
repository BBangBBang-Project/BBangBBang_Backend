package test.bbang.Dto.Cart;

import lombok.Data;
import test.bbang.Entity.CartItem;

@Data
// 장바구니 상품 불러오기 대한 응답 DTO
public class CartItemResponseDto {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private Double price;
    private Integer quantity;
    private String imageUrl;
    private Double totalPrice;

    public CartItemResponseDto() {
    }

    public CartItemResponseDto(CartItem cartItem) {
        this.cartItemId = cartItem.getId();
        this.productId = cartItem.getBread().getId();
        this.productName = cartItem.getBread().getName();
        this.price = (double) cartItem.getBread().getPrice();
        this.quantity = cartItem.getQuantity();
        this.totalPrice = this.price * this.quantity;
    }
}
