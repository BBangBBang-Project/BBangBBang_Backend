package test.bbang.Dto.Bread;

import lombok.Data;

@Data
public class BreadLoadListDto {
    private Long id;
    private String name;
    private int popularityScore;
    private Integer price;
    private Integer stock;
    private String imageUrl;

    public BreadLoadListDto() {
    }

    public BreadLoadListDto(Long id, String name, Integer price, Integer stock, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;

    }

    public BreadLoadListDto(Long id, String name, Integer price, Integer stock, String imageUrl, Integer popularityScore) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.popularityScore = popularityScore;

    }
}
