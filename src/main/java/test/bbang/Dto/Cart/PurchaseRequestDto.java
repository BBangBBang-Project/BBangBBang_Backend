package test.bbang.Dto.Cart;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PurchaseRequestDto {
    private List<Long> breadIds;
    private Map<Long, Integer> quantities; // 상품 ID와 수량 매핑
}
