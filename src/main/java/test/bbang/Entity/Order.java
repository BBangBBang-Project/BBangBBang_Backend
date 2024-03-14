package test.bbang.Entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Long userId;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Bread> breadList;

    private boolean pickState;

    public Order(List<Bread> breadList) {
        this.breadList = breadList;
    }

    public Order() {
    }
}
