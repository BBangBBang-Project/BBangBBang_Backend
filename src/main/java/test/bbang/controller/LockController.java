package test.bbang.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class LockController {

    // 일단 내 핫스팟으로 연결된 라즈베리파이의 주소로 설정했다.
    private final String RASPBERRY_PI_ENDPOINT = "https://raspberrypi.ngrok.io";

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/lock")
    public ResponseEntity<String> lock() {
        // 라즈베리파이 서버로 '잠금' 명령을 보내는 코드
        String response = restTemplate.postForObject(RASPBERRY_PI_ENDPOINT + "/lock", null, String.class);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/unlock")
    public ResponseEntity<String> unlock() {
        // 라즈베리파이 서버로 '잠금 해제' 명령을 보내는 코드
        String response = restTemplate.postForObject(RASPBERRY_PI_ENDPOINT + "/unlock", null, String.class);
        return ResponseEntity.ok(response);
    }
}
