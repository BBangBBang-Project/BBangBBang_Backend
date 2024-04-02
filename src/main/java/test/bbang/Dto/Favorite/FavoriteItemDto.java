package test.bbang.Dto.Favorite;

import lombok.Data;

@Data
public class FavoriteItemDto {
    private Long favorite_id;
    private Long productId;
    private String name;
    private int price;

    public FavoriteItemDto(){

    }

    public FavoriteItemDto(Long favorite_id, Long productId, String name, int price){
        this.favorite_id= favorite_id;
        this.productId =productId;
        this.name =name;
        this.price =price;
    }
}
