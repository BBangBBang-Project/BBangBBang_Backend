package test.bbang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.bbang.Dto.Bread.SoldBreadDto;
import test.bbang.Dto.Order.OrderResponseDto;
import test.bbang.Dto.Order.WeeklySalesDto;
import test.bbang.Entity.Order;
import test.bbang.repository.OrderItemRepository;
import test.bbang.repository.OrderRepository;
import test.bbang.service.OrderService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/seller")
public class SellerController {

    private final OrderItemRepository orderItemRepository;

    private final OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    public SellerController(OrderItemRepository orderItemRepository, OrderRepository orderRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/bread")
    public ResponseEntity<List<SoldBreadDto>> getSoldBreads() {
        List<SoldBreadDto> soldBreads = orderItemRepository.findSoldBreads();
        return ResponseEntity.ok(soldBreads);
    }

    @GetMapping("/sales")
    public WeeklySalesDto getWeeklySales() {
        return orderService.getWeeklySalesStatistics();
    }

    // 모든 주문 내역 조회
    // 모든 주문 내역 조회, 날짜 순으로 오름차순 정렬
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        List<Order> orders = orderRepository.findAllByOrderByOrderDateAsc();
        List<OrderResponseDto> orderDtos = orders.stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDtos);
    }
}
