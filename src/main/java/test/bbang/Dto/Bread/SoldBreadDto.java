package test.bbang.Dto.Bread;

import lombok.Data;

@Data
public class SoldBreadDto {
    private String breadName;
    private long quantitySold;

    public SoldBreadDto(String breadName, long quantitySold) {
        this.breadName = breadName;
        this.quantitySold = quantitySold;
    }

    // Getter 및 Setter
}
