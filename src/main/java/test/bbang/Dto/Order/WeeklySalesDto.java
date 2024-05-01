package test.bbang.Dto.Order;

import lombok.Data;

import java.util.List;

@Data
public class WeeklySalesDto {
    private double lastWeekTotalSales;
    private double thisWeekTotalSales;
    private List<BreadSalesDto> lastWeekBreadSales;
    private List<BreadSalesDto> thisWeekBreadSales;

    @Data
    public static class BreadSalesDto {
        private Long breadId;
        private String breadName;
        private int quantity;
    }
}

