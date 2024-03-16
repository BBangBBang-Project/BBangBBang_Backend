package test.bbang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.bbang.Dto.Bread.BreadLoadListDto;
import test.bbang.Dto.Cart.CartItemRequestDto;
import test.bbang.Dto.Cart.CartItemResponseDto;
import test.bbang.Dto.Cart.PurchaseRequestDto;
import test.bbang.Dto.Cart.PurchaseResponseDto;
import test.bbang.Dto.Customer.SignInRequest;
import test.bbang.Dto.Customer.SignUpRequest;
import test.bbang.Dto.Order.OrderResponseDto;
import test.bbang.Entity.Customer;
import test.bbang.service.BreadService;
import test.bbang.service.CartService;
import test.bbang.service.CustomerService;
import test.bbang.service.OrderService;


import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final BreadService breadService;
    private final CartService cartService;
    private final OrderService orderService;

    //스프링이 알아서 필요한 객체를 찾아서 주입해줌, 해당 서비스의 인스턴스를 자동으로 주입받는다는 뜻.
    @Autowired
    public CustomerController(CustomerService customerService, BreadService breadService,
                              CartService cartService, OrderService orderService) {
        this.customerService = customerService;
        this.breadService = breadService;
        this.cartService = cartService;
        this.orderService = orderService;
    }


    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest signUpRequest) {
        String result = customerService.signUp(signUpRequest.getUsername(), signUpRequest.getPassword(),
                signUpRequest.getName(), signUpRequest.getQuickPassword());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/signIn")
    public ResponseEntity<String> signIn(@RequestBody SignInRequest signInRequest) {
        String result = customerService.signIn(signInRequest.getUsername(), signInRequest.getPassword());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/products")
    public ResponseEntity<List<BreadLoadListDto>> getBreadList() {
        List<BreadLoadListDto> breads = breadService.listAllBreads();
        return ResponseEntity.ok(breads);
    }

    // 장바구니에 상품 추가
    @PostMapping("/{customerId}/cart")
    public ResponseEntity<?> addToCart(@PathVariable Long customerId, @RequestBody CartItemRequestDto cartItemRequestDto) {
        CartItemResponseDto cartItemResponseDto = cartService.addToCart(customerId, cartItemRequestDto);
        return ResponseEntity.ok(cartItemResponseDto);
    }

    // 장바구니 내역 조회
    @GetMapping("/{customerId}/cart")
    public ResponseEntity<List<CartItemResponseDto>> getCart(@PathVariable Long customerId) {
        Customer customer = customerService.getCustomerById(customerId); // 고객 정보 조회
        List<CartItemResponseDto> cartItems = cartService.getCartItems(customer); // 고객 객체를 인자로 전달
        return ResponseEntity.ok(cartItems);
    }

    // 특정 고객의 장바구니에 있는 상품들을 구매한다.
    @PostMapping("/{customerId}/cart/purchase")
    public ResponseEntity<OrderResponseDto> purchaseCartItems(@PathVariable Long customerId, @RequestBody PurchaseRequestDto purchaseRequestDto) {
        OrderResponseDto orderResponseDto = cartService.purchaseCartItems(customerId, purchaseRequestDto);
        return ResponseEntity.ok(orderResponseDto);
    }

    // 구매 내역을 불러온다.
    @GetMapping("/{customerId}/orders")
    public ResponseEntity<List<OrderResponseDto>> getOrders(@PathVariable Long customerId) {
        List<OrderResponseDto> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

}