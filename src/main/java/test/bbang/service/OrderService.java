package test.bbang.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.bbang.Dto.Bread.BreadPurchaseDto;
import test.bbang.Dto.Bread.SoldBreadDto;
import test.bbang.Dto.Order.CheckoutDto;
import test.bbang.Dto.Order.OrderDto;
import test.bbang.Dto.Order.OrderResponseDto;
import test.bbang.Dto.Order.WeeklySalesDto;
import test.bbang.Entity.*;
import test.bbang.repository.BreadRepository;
import test.bbang.repository.CustomerRepository;
import test.bbang.repository.OrderItemRepository;
import test.bbang.repository.OrderRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger LOGGER = Logger.getLogger(OrderService.class.getName());
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private  final OrderItemRepository orderItemRepository;
    private final BreadRepository breadRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                            CustomerRepository customerRepository,BreadRepository breadRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.breadRepository = breadRepository;
        this.orderItemRepository = orderItemRepository;
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

    public WeeklySalesDto getWeeklySalesStatistics() {
        LocalDate now = LocalDate.now();
        LocalDate startOfThisWeek = now.with(java.time.DayOfWeek.MONDAY);
        LocalDate startOfLastWeek = startOfThisWeek.minusWeeks(1);
        LocalDate endOfLastWeek = startOfThisWeek.minusDays(1);

        // 날짜 로그를 기록
        LOGGER.info("This Week Starts: " + startOfThisWeek);
        LOGGER.info("Last Week Starts: " + startOfLastWeek);
        LOGGER.info("Last Week Ends: " + endOfLastWeek);

        List<Order> thisWeekOrders = orderRepository.findAllWithOrderDateBetween(startOfThisWeek.atStartOfDay(), LocalDateTime.now());
        List<Order> lastWeekOrders = orderRepository.findAllWithOrderDateBetween(startOfLastWeek.atStartOfDay(), endOfLastWeek.atTime(23, 59));

        return createWeeklySalesDto(thisWeekOrders, lastWeekOrders);
    }

    private WeeklySalesDto createWeeklySalesDto(List<Order> thisWeekOrders, List<Order> lastWeekOrders) {
        WeeklySalesDto weeklySalesDto = new WeeklySalesDto();

        // 이번 주 판매 통계
        weeklySalesDto.setThisWeekTotalSales(thisWeekOrders.stream().mapToDouble(Order::getTotalPrice).sum());
        weeklySalesDto.setThisWeekBreadSales(extractBreadSales(thisWeekOrders));

        // 지난 주 판매 통계
        weeklySalesDto.setLastWeekTotalSales(lastWeekOrders.stream().mapToDouble(Order::getTotalPrice).sum());
        weeklySalesDto.setLastWeekBreadSales(extractBreadSales(lastWeekOrders));

        return weeklySalesDto;
    }

    private List<WeeklySalesDto.BreadSalesDto> extractBreadSales(List<Order> orders) {
        Map<Long, Integer> breadIdToQuantityMap = new HashMap<>();

        // 주문 항목에서 빵 ID와 수량을 추출하여 Map에 저장합니다. 같은 ID를 가진 빵의 수량은 합산됩니다.
        orders.forEach(order -> order.getOrderItems().forEach(orderItem -> {
            Bread bread = orderItem.getBread();
            breadIdToQuantityMap.merge(bread.getId(), orderItem.getQuantity(), Integer::sum);
        }));

        // 최종 결과를 저장할 리스트를 생성합니다.
        List<WeeklySalesDto.BreadSalesDto> breadSales = new ArrayList<>();

        // Map에 저장된 정보를 바탕으로 최종 결과 리스트를 생성합니다.
        breadIdToQuantityMap.forEach((breadId, quantity) -> {
            // 빵 ID를 사용하여 빵 이름을 조회합니다.
            Bread bread = breadRepository.findById(breadId).orElseThrow(() -> new RuntimeException("Bread not found: " + breadId));

            WeeklySalesDto.BreadSalesDto dto = new WeeklySalesDto.BreadSalesDto();
            dto.setBreadId(bread.getId());
            dto.setBreadName(bread.getName());
            dto.setQuantity(quantity);

            breadSales.add(dto);
        });

        return breadSales;
    }

}

