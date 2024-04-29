package test.bbang.Dto.Order;


import lombok.Data;
import test.bbang.Dto.Bread.BreadPurchaseDto;

import java.util.List;

@Data
public class OrderDto {

    private Long orderId;
    private List<BreadPurchaseDto> breadPurchaseDtoList;
    private boolean pickStatus = false;
    public OrderDto(List<BreadPurchaseDto> breadPurchaseDtoList) {
        this.breadPurchaseDtoList = breadPurchaseDtoList;
    }

}
