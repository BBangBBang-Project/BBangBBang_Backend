package test.bbang.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.bbang.Dto.Bread.BreadLoadListDto;
import test.bbang.Dto.Bread.BreadPurchaseDto;
import test.bbang.Dto.Bread.BreadRegisterDto;
import test.bbang.Dto.Bread.SoldBreadDto;
import test.bbang.Dto.Order.OrderDto;
import test.bbang.Entity.Bread;
import test.bbang.Entity.Order;
import test.bbang.Entity.OrderItem;
import test.bbang.repository.BreadRepository;
import test.bbang.repository.CustomerRepository;
import test.bbang.repository.OrderRepository;
import test.bbang.service.BreadService;
import test.bbang.service.CustomerService;
import test.bbang.service.KioskService;
import test.bbang.service.OrderService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/kiosk")
@Slf4j
public class KioskController {

    private final BreadRepository breadRepository;
    private final OrderRepository orderRepository;
    private final KioskService kioskService;

    private final OrderService orderService;
    private final BreadService breadService;

    public KioskController(BreadRepository breadRepository,
                           OrderRepository orderRepository,
                           KioskService kioskService,
                           OrderService orderService,
                           BreadService breadService) {
        this.breadRepository = breadRepository;
        this.orderRepository = orderRepository;
        this.kioskService = kioskService;
        this.orderService = orderService;
        this.breadService = breadService;
    }

    //모든 빵 리스트 보기
    @GetMapping("/bread")
    public List<BreadLoadListDto> getBreadList(){
        return breadService.listAllBreads();
    }

    //특정 빵 정보 확인
    @GetMapping("/bread/{productId}")
    public ResponseEntity<BreadRegisterDto> getBread(@PathVariable("productId") Long productId){

        // 빵DB에서 Id로 검색하여 가져온다.
        Optional<Bread> findBread = breadRepository.findById(productId);
        if(findBread.isPresent()){
            Bread bread = findBread.get();
            return ResponseEntity.ok().body(breadService.convertToBreadDto(bread));
        }
        else {
            log.info("상품 ID={}에 해당하는 정보를 찾을 수 없습니다.",productId);
            return ResponseEntity.notFound().build(); //404, 메세지 본문 비어있음
        }
    }

    //판매자 빵 등록
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
            orderService.convertToOrder(orderDto);
            return ResponseEntity.ok().body("빵이 구매되었습니다.\n");
        }
        else {
            return ResponseEntity.badRequest().body("빵이 구매되지 않았습니다.");
        }
    }

    @PostMapping("/pick/{quickPassword}")
    public ResponseEntity<?> pickUpBread(@PathVariable("quickPassword") String quickPassword){

        List<SoldBreadDto> breadList = kioskService.findByQuickPassword(quickPassword);
        if (!breadList.isEmpty()){
            return ResponseEntity.ok(breadList);
        }
        else{
            return ResponseEntity.badRequest().body("비어 있음");
        }
    }

    @PostMapping("pick/bread/{orderId}") //픽업 완료 버튼 누르기
    public ResponseEntity<?> completePickUp(@PathVariable("orderId") Long orderId){

        if(kioskService.setPickStatus(orderId))
            return ResponseEntity.ok().body("픽업 완료!");
        else return ResponseEntity.badRequest().body("픽업 실패!");

    }



}
