package test.bbang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.bbang.Dto.Bread.SoldBreadDto;
import test.bbang.Dto.Order.WeeklySalesDto;
import test.bbang.repository.OrderItemRepository;
import test.bbang.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/seller")
public class SellerController {

    private final OrderItemRepository orderItemRepository;

    @Autowired
    private OrderService orderService;

    public SellerController(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
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
}
