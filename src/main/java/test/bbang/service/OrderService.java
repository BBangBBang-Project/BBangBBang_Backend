package test.bbang.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.bbang.Dto.Bread.BreadPurchaseDto;
import test.bbang.Dto.Bread.SoldBreadDto;
import test.bbang.Dto.Order.CheckoutDto;
import test.bbang.Dto.Order.OrderDto;
import test.bbang.Dto.Order.OrderResponseDto;
import test.bbang.Entity.*;
import test.bbang.repository.BreadRepository;
import test.bbang.repository.CustomerRepository;
import test.bbang.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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

    public Long convertToOrder(OrderDto orderDto) {

        List<BreadPurchaseDto> breadPurchaseDtoList = orderDto.getBreadPurchaseDtoList();

        //kiosk를 1번으로 고정 시킬 것임.
        Customer customer = customerRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Order order = new Order();

        order.setCustomer(customer);


        List<OrderItem> orderItems = breadPurchaseDtoList.stream()
                .map(breadPurchaseDto -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    Optional<Bread> byId = breadRepository.findById(breadPurchaseDto.getId());
                    if (byId.isPresent()){
                        Bread bread = byId.get();
                        orderItem.setBread(bread);
                        orderItem.setPrice(bread.getPrice() * breadPurchaseDto.getCount());
                        orderItem.setQuantity(breadPurchaseDto.getCount());
                    }else{
                        throw new ResourceNotFoundException("Bread not found with id: " + breadPurchaseDto.getId());
                    }
                    return orderItem;
                }).collect(Collectors.toList());

        order.setOrderItems(orderItems);
        order.calculateTotalPrice();
        System.out.println(order.getTotalPrice());

        orderRepository.save(order);
        return order.getOrderId();


    }

    public CheckoutDto prepareCheckout(BreadPurchaseDto breadPurchaseDto) {

        Bread bread = breadRepository.findById(breadPurchaseDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Bread not found"));
        System.out.println("breadPurchaseDto = " + breadPurchaseDto);
        // 결제 정보 생성
        CheckoutDto checkoutDto = new CheckoutDto();
        checkoutDto.setBreadId(bread.getId());
        checkoutDto.setBreadName(bread.getName());
        checkoutDto.setBreadImage(bread.getImagePath());
        checkoutDto.setQuantity(breadPurchaseDto.getCount());
        checkoutDto.setPrice(bread.getPrice());
        checkoutDto.setTotalPrice(bread.getPrice() * breadPurchaseDto.getCount());

        return checkoutDto;
    }

    //구매자앱에서 하나의 상품을 원하는 수량만큼 바로 구매하기 했을 경우
    @Transactional
    public OrderResponseDto purchaseSingleBreadItem(Long customerId, BreadPurchaseDto breadPurchaseDto) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Bread bread = breadRepository.findById(breadPurchaseDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Bread not found"));

        // 재고 확인
        if (bread.getStock() < breadPurchaseDto.getCount()) {
            throw new IllegalStateException("Not enough stock for the bread");
        }

        // 재고 차감
        bread.setStock(bread.getStock() - breadPurchaseDto.getCount());
        breadRepository.save(bread);

        // 주문 생성
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());

        // 주문 항목 생성
        OrderItem orderItem = new OrderItem();
        orderItem.setBread(bread);
        orderItem.setQuantity(breadPurchaseDto.getCount());
        orderItem.setPrice(bread.getPrice() * breadPurchaseDto.getCount());
        orderItem.setOrder(order);

        // 주문에 주문 항목 추가
        order.setOrderItems(Collections.singletonList(orderItem));
        order.calculateTotalPrice(); // Total price 계산 메서드는 구현되어야 함

        orderRepository.save(order);

        return new OrderResponseDto(order);
    }

    public List<SoldBreadDto> findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> order.getOrderItems().stream()
                        .map(orderItem -> new SoldBreadDto(orderItem.getBread().getName(), orderItem.getQuantity()))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }


}

