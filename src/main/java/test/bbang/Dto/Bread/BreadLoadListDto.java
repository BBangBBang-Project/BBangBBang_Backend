package test.bbang.Dto.Bread;

import lombok.Data;

@Data
public class BreadLoadListDto {
    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private String imageUrl;
}
