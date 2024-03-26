package test.bbang.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Bread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int price;
    private int stock;
    private String imagePath;

    public Bread() {
    }
    public Bread(String name, int price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
}
