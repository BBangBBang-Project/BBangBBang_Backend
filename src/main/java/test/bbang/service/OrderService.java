package test.bbang.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.bbang.Dto.Bread.BreadPurchaseDto;
import test.bbang.Dto.Order.OrderDto;
import test.bbang.Dto.Order.OrderResponseDto;
import test.bbang.Entity.Bread;
import test.bbang.Entity.Customer;
import test.bbang.Entity.Order;
import test.bbang.Entity.OrderItem;
import test.bbang.repository.BreadRepository;
import test.bbang.repository.CustomerRepository;
import test.bbang.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    private final BreadRepository breadRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                            CustomerRepository customerRepository,BreadRepository breadRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.breadRepository = breadRepository;
    }

    public List<OrderResponseDto> getOrdersByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        List<Order> orders = orderRepository.findByCustomer(customer);
        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found for the customer");
        }

        return orders.stream()
                .map(order -> new OrderResponseDto(order)) // 수정된 생성자를 사용합니다.
                .collect(Collectors.toList());
    }

//    public Order convertToOrder(List<BreadPurchaseDto> breadPurchaseDto) {
//
//        List<OrderItem> orderItemList = new ArrayList<>();
//
//        for (BreadPurchaseDto purchaseDto : breadPurchaseDto) {
//            OrderItem orderItem = new OrderItem();
//            orderItem.setOrderItemId(purchaseDto.getId());
//            orderItem.setQuantity(purchaseDto.getCount());
//        }
//
//        return new Order();
//    }
}

