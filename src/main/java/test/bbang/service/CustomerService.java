package test.bbang.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import test.bbang.Dto.Favorite.FavoriteItemDto;
import test.bbang.Entity.*;
import test.bbang.repository.BreadRepository;
import test.bbang.repository.CustomerRepository;
import test.bbang.repository.FavoriteRepository;
import test.bbang.repository.OrderRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private BreadRepository breadRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
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


    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    public FavoriteItemDto addBreadToFavorites(Long customerId, Long breadId) throws ResourceNotFoundException {
        // 고객 정보 조회
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // 빵 정보 조회
        Bread bread = breadRepository.findById(breadId)
                .orElseThrow(() -> new ResourceNotFoundException("Bread not found"));

        // 고객의 찜 목록 조회 및 생성
        Favorite favorite = favoriteRepository.findByCustomer(customer)
                .orElseGet(() -> createFavorite(customer));

        // 찜 목록에 있는 동일한 빵의 FavoriteItem 조회
        Optional<FavoriteItem> existingFavoriteItem = favorite.getItems().stream()
                .filter(item -> item.getBread().equals(bread))
                .findFirst();

        FavoriteItem favoriteItem;

        // 동일한 빵이 찜 목록에 이미 있는 경우 처리하지 않음
        if (!existingFavoriteItem.isPresent()) {
            // 찜 목록에 동일한 빵이 없는 경우 새로운 FavoriteItem 생성
            favoriteItem = new FavoriteItem();
            favoriteItem.setFavorite(favorite);
            favoriteItem.setBread(bread);
            favorite.getItems().add(favoriteItem);
            favoriteRepository.save(favorite); // 찜 목록 저장
        } else {
            favoriteItem = existingFavoriteItem.get();
        }

        // FavoriteItemResponseDTO 생성 및 반환
        FavoriteItemDto favoriteItemDto = new FavoriteItemDto();
        favoriteItemDto.setFavorite_id(favoriteItem.getId());
        favoriteItemDto.setProductId(favoriteItem.getBread().getId());
        favoriteItemDto.setName(bread.getName());
        favoriteItemDto.setPrice(bread.getPrice());

        return favoriteItemDto;
    }

    private Favorite createFavorite(Customer customer) {
        Favorite favorite = new Favorite();
        favorite.setCustomer(customer);
        return favoriteRepository.save(favorite);
    }

    public Boolean removeBreadFromFavorites(Long customerId, Long breadId) throws ResourceNotFoundException {
        // 고객 정보 조회
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // 빵 정보 조회
        Bread bread = breadRepository.findById(breadId)
                .orElseThrow(() -> new ResourceNotFoundException("Bread not found"));

        // 고객의 찜 목록 조회
        Favorite favorite = favoriteRepository.findByCustomer(customer)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));

        // 찜 목록에서 해당 빵을 찾음
        Optional<FavoriteItem> favoriteItem = favorite.getItems().stream()
                .filter(item -> item.getBread().equals(bread))
                .findFirst();

        // 해당 빵을 찜 목록에서 삭제하고 결과 반환
        if(favoriteItem.isPresent()) {
            favorite.getItems().remove(favoriteItem.get());
            favoriteRepository.save(favorite);
            return true; // 삭제 성공
        } else {
            return false; // 삭제 실패, 목록에 없음
        }
    }

    public List<FavoriteItemDto> getFavorites(Long customerId) throws ResourceNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Favorite favorite = favoriteRepository.findByCustomer(customer)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));

        // 찜 목록에서 FavoriteItem 리스트를 FavoriteItemResponseDTO 리스트로 변환
        return favorite.getItems().stream()
                .map(item -> new FavoriteItemDto(item.getId(), item.getBread().getId(), item.getBread().getName(), item.getBread().getPrice(), getImageUrl(item.getBread().getImagePath())))
                .collect(Collectors.toList());
    }

    public String getImageUrl(String imagePath) {
        // 웹 서버의 도메인 또는 IP 주소
        String baseUrl = "http://localhost:8080";

        // imagePath가 null이 아닐 때만 처리
        if (imagePath != null && !imagePath.isEmpty()) {
            // 이미지 경로에서 'uploads' 디렉토리까지의 경로를 제거하고, URL 생성
            String imageUrl = baseUrl + "/images/" + imagePath.substring(imagePath.lastIndexOf("uploads") + 8);
            return imageUrl;
        } else {
            // imagePath가 null이거나 빈 문자열일 경우 기본 이미지 URL을 반환하거나, null을 반환
            // 예: 기본 이미지를 가리키는 URL이나, null을 반환하거나, 혹은 적절한 처리를 할 수 있습니다.
            return baseUrl + "/images/default-image.png"; // 예시로 기본 이미지 경로를 설정
        }
    }
}

