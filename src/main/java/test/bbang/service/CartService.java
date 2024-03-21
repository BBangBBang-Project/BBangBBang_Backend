package test.bbang.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.bbang.Dto.Cart.CartItemRequestDto;
import test.bbang.Dto.Cart.CartItemResponseDto;
import test.bbang.Dto.Order.OrderResponseDto;
import test.bbang.Entity.*;
import test.bbang.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final BreadRepository breadRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public CartService(BreadRepository breadRepository, CartRepository cartRepository,
                       CartItemRepository cartItemRepository, CustomerRepository customerRepository,
                       OrderRepository orderRepository) {
        this.breadRepository = breadRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    public CartItemResponseDto addToCart(Long customerId, CartItemRequestDto cartItemRequestDto) {
        // 빵 정보 조회
        Bread bread = breadRepository.findById(cartItemRequestDto.getBreadId())
                .orElseThrow(() -> new ResourceNotFoundException("Bread not found"));

        // 고객 정보 조회
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // 고객의 장바구니 조회 및 생성
        Cart cart = cartRepository.findByCustomer(customer)
                .orElseGet(() -> createCart(customer));

        // 장바구니에 있는 동일한 빵의 CartItem 조회
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndBread(cart, bread);

        CartItem cartItem;

        // 동일한 빵이 장바구니에 이미 있는 경우
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartItemRequestDto.getQuantity());
        } else {
            // 장바구니에 동일한 빵이 없는 경우 새로운 CartItem 생성
            cartItem = createCartItem(cart, bread, cartItemRequestDto.getQuantity());
            cart.getItems().add(cartItem); // Cart에 CartItem 추가
        }

        // CartItem 저장
        cartItemRepository.save(cartItem);

        // Cart 저장 (영속성 컨텍스트 내의 cart가 업데이트 됩니다)
        cartRepository.save(cart);

        // DTO 생성 및 반환
        return createCartItemResponseDto(cartItem);
    }

    private Cart createCart(Customer customer) {
        Cart cart = new Cart();
        cart.setCustomer(customer);
        cartRepository.save(cart);
        return cart;
    }

    private CartItem createCartItem(Cart cart, Bread bread, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setBread(bread);
        cartItem.setQuantity(quantity);
        return cartItem;
    }

    private CartItemResponseDto createCartItemResponseDto(CartItem cartItem) {
        // DTO 생성 로직
        CartItemResponseDto dto = new CartItemResponseDto(cartItem);
        dto.setProductId(cartItem.getBread().getId());
        dto.setProductName(cartItem.getBread().getName());
        dto.setPrice((double) cartItem.getBread().getPrice());
        dto.setQuantity(cartItem.getQuantity());
        dto.setTotalPrice((double) (cartItem.getBread().getPrice() * cartItem.getQuantity()));
        // ... 다른 필요한 필드 설정
        return dto;
    }

    public List<CartItemResponseDto> getCartItems(Customer customer) {
        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        return cart.getItems().stream() // 'items'는 Cart 엔터티의 필드명과 일치해야 합니다.
                .map(CartItemResponseDto::new) // 수정: 생성자를 사용하여 DTO를 생성합니다.
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponseDto purchaseCartItems(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = new Order();
        order.setCustomer(customer);

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {

                    Bread bread = cartItem.getBread();
                    if (bread.getStock() < cartItem.getQuantity()) {
                        throw new IllegalStateException("Not enough stock for bread " + bread.getName());
                    }
                    // 재고 감소
                    bread.setStock(bread.getStock() - cartItem.getQuantity());
                    breadRepository.save(bread); // 변경된 재고를 저장

                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setBread(cartItem.getBread());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getBread().getPrice() * cartItem.getQuantity());
                    return orderItem;
                }).collect(Collectors.toList());

        order.setOrderItems(orderItems);
        order.calculateTotalPrice();

        orderRepository.save(order);
        cart.getItems().clear();
        cartRepository.save(cart);

        return new OrderResponseDto(order);
    }

}

