package test.bbang.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.bbang.Dto.Bread.BreadPurchaseDto;
import test.bbang.Dto.Bread.BreadRegisterDto;
import test.bbang.Dto.Order.OrderDto;
import test.bbang.Entity.Bread;
import test.bbang.Entity.Customer;
import test.bbang.Entity.Order;
import test.bbang.repository.BreadRepository;
import test.bbang.repository.CustomerRepository;
import test.bbang.repository.OrderRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KioskService {
    private final BreadRepository breadRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final BreadService breadService;

    public KioskService(BreadRepository breadRepository,
                        OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        BreadService breadService) {
        this.breadRepository = breadRepository;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.breadService = breadService;
    }


    @Transactional
    public boolean saveBread(BreadRegisterDto breadRegisterDto){

        try {
            // 중복되는 이름의 상품이 있는 지 검색
            Optional<Bread> optionalBread = findByName(breadRegisterDto.getName());

            if (optionalBread.isPresent()) {
                // 이미 같은 이름의 빵이 존재한다면 false 반환
                return false;
            } else {
                // 같은 이름의 빵이 존재하지 않을 경우, 새 빵 객체를 생성하고 저장
                Bread bread = breadService.convertToBread(breadRegisterDto);
                breadRepository.save(bread);
                return true;
            }
        } catch (Exception e) {
            log.error("saveBread 오류", e);
            return false;
        }


    }

    public Optional<Bread> findByName(String name) {
        return breadRepository.findByName(name);
    }



    public boolean updateBread(Long productId, BreadRegisterDto breadRegisterDto) {
        Optional<Bread> breadOptional = breadRepository.findById(productId);
        breadOptional.ifPresent(bread -> {
            bread.setName(breadRegisterDto.getName());
            bread.setPrice(breadRegisterDto.getPrice());
            bread.setStock(breadRegisterDto.getStock());
            breadRepository.save(bread);
        });
        return breadOptional.isPresent();
    }

    public boolean deleteBread(Long productId) {
        if (breadRepository.existsById(productId)) {
            breadRepository.deleteById(productId);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public boolean orderCheck(OrderDto orderDto) {
        //iter를 돌며 빵의 재고가 부족하지 않은 지 검색.
        for (BreadPurchaseDto breadPurchaseDto : orderDto.getBreadPurchaseDtoList()) {
            Optional<Bread> findBread = breadRepository.findById(breadPurchaseDto.getId());
            if (findBread.isPresent()) {
                Bread bread = findBread.get();

                if (bread.getStock() >= breadPurchaseDto.getCount()) {
                    bread.setStock(bread.getStock() - breadPurchaseDto.getCount());

                } else {
                    log.info("재고가 부족합니다. 상품 ID={}", breadPurchaseDto.getId());
                    return false;
                }
            } else {
                log.info("상품을 찾을 수 없습니다. 상품 ID={}", breadPurchaseDto.getId());
                return false;
            }
        }
        return true;
    }

    //고객 앱에서 주문을 생성하는 경우
    public List<Bread> findByQuickPassword(String quickPassword){
        //간편 비밀번호를 유저 테이블에서 찾음
        Optional<Customer> byQuickPassword = customerRepository.findByQuickPassword(quickPassword);
        //찾으면 픽업 목록 보여줌
        if (byQuickPassword.isPresent()) {
            Customer customer = byQuickPassword.get();
            //로그 찍어보려고 간단 작성
            log.info("name={},  password={}, username={}", customer.getName(),
                    customer.getPassword(), customer.getUsername());

            List<Order> orders = orderRepository.findByCustomer(customer);

            if(!orders.isEmpty()){
                return orders.stream()
                        .filter(order -> order.isPickState())
                        .flatMap(order -> order.getBreadList().stream())
                        .toList();
            }
        }
        return Collections.emptyList();
    }


}
