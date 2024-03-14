package test.bbang.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.bbang.Dto.Bread.BreadPurchaseDto;
import test.bbang.Dto.Bread.BreadRegisterDto;
import test.bbang.Dto.OrderDto;
import test.bbang.Entity.Bread;
import test.bbang.Entity.Order;
import test.bbang.repository.BreadRepository;
import test.bbang.repository.CustomerRepository;
import test.bbang.repository.OrderRepository;
import test.bbang.service.CustomerService;
import test.bbang.service.KioskService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/kiosk")
@Slf4j
public class KioskController {

    private final CustomerRepository customerRepository;
    private final BreadRepository breadRepository;

    private final OrderRepository orderRepository;
    private final KioskService kioskService;

    private final CustomerService customerService;

    public KioskController(CustomerRepository customerRepository,
                           BreadRepository breadRepository,
                           OrderRepository orderRepository,
                           KioskService kioskService,
                           CustomerService customerService) {
        this.customerRepository = customerRepository;
        this.breadRepository = breadRepository;
        this.orderRepository = orderRepository;
        this.kioskService = kioskService;
        this.customerService = customerService;
    }

    @GetMapping("/bread")
    public List<Bread> getBreadList(){
        return kioskService.findAllBreads();
    }

    @GetMapping("/bread/{productId}")
    public ResponseEntity<BreadRegisterDto> getBread(@PathVariable("productId") Long productId){
        //Id에 맞는 DB에서 빵 데이터를 가져옴

        Optional<Bread> findBread = breadRepository.findById(productId);
        if(findBread.isPresent()){
            Bread bread = findBread.get();
            return ResponseEntity.ok().body(kioskService.convertToBreadDto(bread));
        }
        else {
            log.info("상품 ID={}에 해당하는 정보를 찾을 수 없습니다.",productId);
            return ResponseEntity.notFound().build(); //404, 메세지 본문 비어있음
        }
    }

    @PostMapping("/bread")
    public ResponseEntity<?> registerBread(@RequestBody BreadRegisterDto breadRegisterDto) {

        boolean isSuccess = kioskService.saveBread(breadRegisterDto);
        if (isSuccess) {
            return ResponseEntity.ok().body("빵 등록에 성공했습니다.");
        } else {
            return ResponseEntity.badRequest().body("빵 등록에 실패했습니다.");
        }
    }

    @PutMapping("/bread/{productId}")
    public ResponseEntity<?> updateBread(@PathVariable("productId") Long productId, @RequestBody BreadRegisterDto breadRegisterDto) {
        boolean isSuccess = kioskService.updateBread(productId, breadRegisterDto);
        if (isSuccess) {
            return ResponseEntity.ok().body("빵 정보 업데이트에 성공했습니다.");
        } else {
            return ResponseEntity.badRequest().body("해당 빵 정보를 찾을 수 없습니다.");
        }
    }

    @DeleteMapping("/bread/{productId}")
    public ResponseEntity<?> deleteBread(@PathVariable("productId") Long productId) {
        boolean isSuccess = kioskService.deleteBread(productId);
        if (isSuccess) {
            return ResponseEntity.ok().body("빵 삭제에 성공했습니다.");
        } else {
            return ResponseEntity.badRequest().body("해당 빵 정보를 찾을 수 없습니다.");
        }
    }


    // "결제하기"버튼을 눌렀을 경우에...
    @PostMapping("/bread/order")
    public ResponseEntity<?> purchaseBread(@RequestBody List<BreadPurchaseDto> breadPurchaseDtoList){

        OrderDto orderDto = new OrderDto(breadPurchaseDtoList);
        if(kioskService.orderCheck(orderDto)){
//            StringBuilder logText = new StringBuilder();
//            for (BreadRegisterDto breadRegisterDto : orderDto.getBreadRegisterDtoList()) {
//                logText.append("Name: ").append(breadRegisterDto.getName())
//                                .append(", Price: ").append(breadRegisterDto.getPrice())
//                                .append(", Stock: ").append(breadRegisterDto.getStock()).append("\n");
//            }
            return ResponseEntity.ok().body("빵이 구매되었습니다.\n");
        }
        else {
            return ResponseEntity.badRequest().body("빵이 구매되지 않았습니다.");
        }
    }

    @PostMapping("/pick/{quickPassword}")
    public ResponseEntity<List<BreadRegisterDto>> pickUpBread(@PathVariable("quickPassword") String quickPassword){

        List<Bread> breadList = customerService.showListByQuickPassword(quickPassword);
        if (!breadList.isEmpty())
            return ResponseEntity.ok(kioskService.convertToDtoList(breadList));
        else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PostMapping("pick/{orderId}")
    public ResponseEntity<?> completePickUp(@PathVariable("orderId") Long orderId){

        Optional<Order> byId = orderRepository.findById(orderId);
        if(byId.isPresent()){
            Order order = byId.get();
            order.setPickState(true);
            return ResponseEntity.ok().body("픽업 완료!");
        }
        return ResponseEntity.badRequest().body("픽업 실패!");

    }


}
