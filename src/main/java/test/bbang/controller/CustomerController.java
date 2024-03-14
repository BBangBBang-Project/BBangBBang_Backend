package test.bbang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.bbang.Dto.Bread.BreadLoadListDto;
import test.bbang.Dto.Customer.SignInRequest;
import test.bbang.Dto.Customer.SignUpRequest;
import test.bbang.service.BreadService;
import test.bbang.service.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final BreadService breadService;

    //스프링이 알아서 필요한 객체를 찾아서 주입해줌, 해당 서비스의 인스턴스를 자동으로 주입받는다는 뜻.
    @Autowired
    public CustomerController(CustomerService customerService, BreadService breadService) {
        this.customerService = customerService;
        this.breadService = breadService;
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

    @GetMapping("/bread")
    public ResponseEntity<List<BreadLoadListDto>> getBreadList() {
        List<BreadLoadListDto> breads = breadService.listAllBreads();
        return ResponseEntity.ok(breads);
    }

}

