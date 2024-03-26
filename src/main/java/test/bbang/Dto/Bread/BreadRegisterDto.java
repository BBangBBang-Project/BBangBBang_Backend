package test.bbang.Dto.Bread;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BreadRegisterDto { //상품의 가격, 상품 이름, 개수, 이미지를 등록할 수 있다.

    private Long id;
    private String name;
    private int price;
    private int stock;
    private MultipartFile imageFile;
    public BreadRegisterDto() {
    }

    public BreadRegisterDto(Long id, String name, int price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public BreadRegisterDto(String name, int price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public BreadRegisterDto(Long id, String name, int price, int stock, MultipartFile imageFile) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.imageFile = imageFile;
    }
}
