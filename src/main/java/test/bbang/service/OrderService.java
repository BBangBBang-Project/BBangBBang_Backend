package test.bbang.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.bbang.Dto.Order.OrderResponseDto;
import test.bbang.Entity.Customer;
import test.bbang.Entity.Order;
import test.bbang.repository.CustomerRepository;
import test.bbang.repository.OrderRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                            CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
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
}

