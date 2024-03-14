package test.bbang.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import test.bbang.Entity.Bread;
import test.bbang.Entity.Customer;
import test.bbang.Entity.Order;
import test.bbang.repository.CustomerRepository;
import test.bbang.repository.OrderRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final OrderRepository orderRepository;

    public CustomerService(CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    public String signUp(String username, String password, String name, String quickPassword) {
        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setPassword(password);
        customer.setName(name);
        customer.setQuickPassword(quickPassword);

        customerRepository.save(customer);
        return "회원 가입에 성공하였습니다!";
    }

    public String signIn(String username, String password) {
        Optional<Customer> customer = customerRepository.findByUsernameAndPassword(username, password);
        if (customer.isPresent()) {
            return "로그인 성공!";
        } else {
            return "로그인 실패: ID 또는 비밀번호가 일치하지 않습니다.";
        }
    }


    //고객 앱에서 주문을 생성하는 경우
    public List<Bread> showListByQuickPassword(String quickPassword){
        //간편 비밀번호를 유저 테이블에서 찾음
        Optional<Customer> byQuickPassword = customerRepository.findByQuickPassword(quickPassword);
        //찾으면 픽업 목록 보여줌
        if (byQuickPassword.isPresent()) {
            Customer customer = byQuickPassword.get();
            //로그 찍어보려고 간단 작성
            log.info("name={},  password={}, username={}", customer.getName(),
                    customer.getPassword(), customer.getUsername());

            Long userId = customer.getUserId();
            Optional<Order> byUserId = orderRepository.findByUserId(userId);
            if (byUserId.isPresent()) {
                Order order = byUserId.get();
                return order.getBreadList();
            }
        }
        return Collections.emptyList();
    }
}

