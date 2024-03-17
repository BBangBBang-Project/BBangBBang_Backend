package test.bbang.Entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "`order`")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "customer_id") // 외래키를 customer_id로 설정
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Bread> breadList;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime orderDate;

    private double totalPrice;

    private boolean pickState;

    public Order(List<Bread> breadList) {
        this.breadList = breadList;
    }

    public Order() {
    }

    @PrePersist
    public void onPrePersist() {
        this.orderDate = LocalDateTime.now();
    }

    // 주문의 총 가격을 계산하는 메서드
    public void calculateTotalPrice() {
        this.totalPrice = orderItems.stream()
                .mapToDouble(OrderItem::getPrice)
                .sum();
    }
}
