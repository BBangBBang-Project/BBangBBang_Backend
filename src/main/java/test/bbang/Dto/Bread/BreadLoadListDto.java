package test.bbang.Dto.Bread;

import lombok.Data;

@Data
public class BreadLoadListDto {
    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private String imageUrl;

    public BreadLoadListDto() {
    }

    public BreadLoadListDto(Long id, String name, Double price, Integer stock, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
    }
}
