package test.bbang.Dto.Order;

import lombok.Data;

@Data
public class CheckoutDto {
    private Long breadId;
    private String breadName;
    private String breadImage;
    private int quantity;
    private double price;
    private double totalPrice;
}

