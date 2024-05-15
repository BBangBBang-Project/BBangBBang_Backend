package test.bbang.service;

import jakarta.persistence.Tuple;
import jakarta.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.bbang.Dto.Bread.BreadLoadListDto;
import test.bbang.Dto.Bread.BreadRegisterDto;
import test.bbang.Entity.Bread;
import test.bbang.repository.BreadRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BreadService {

    private final BreadRepository breadRepository;

    @Autowired
    public BreadService(BreadRepository breadRepository){
        this.breadRepository = breadRepository;
    }
    public List<BreadLoadListDto> listAllBreads() {
        return breadRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 빵의 인기도 목록을 찾는 메소드
    public List<BreadLoadListDto> findBreadPopularity() {
        List<Tuple> tuples = breadRepository.findBreadPopularityNative();
        return tuples.stream().map(this::convertToBreadLoadListDto).collect(Collectors.toList());
    }
    private BreadLoadListDto convertToDto(Bread bread) {
        BreadLoadListDto dto = new BreadLoadListDto();
        dto.setId(bread.getId());
        dto.setName(bread.getName());
        dto.setPrice(bread.getPrice());
        dto.setStock(bread.getStock());
        log.debug("Converting bread entity to DTO. Bread imagePath: {}", bread.getImagePath()); // 로그 추가
        String imageUrl = getImageUrl(bread.getImagePath());
        dto.setImageUrl(imageUrl);
        log.debug("Image URL for bread {}: {}", bread.getId(), imageUrl); // 로그 추가
        return dto;
    }

    private BreadLoadListDto convertToBreadLoadListDto(Tuple tuple) {
        // 다른 필드 처리는 동일하게 유지
        // popularityScore를 안전하게 처리하기 위해 null 체크를 추가
        BigDecimal popularityScoreBigDecimal = tuple.get("popularityScore", BigDecimal.class);
        Integer popularityScore = (popularityScoreBigDecimal != null) ? popularityScoreBigDecimal.intValue() : 0;

        // imagePath를 Tuple에서 추출
        String imagePath = tuple.get("imageUrl", String.class);
        // getImageUrl 메소드를 사용하여 완전한 이미지 URL 생성
        String imageUrl = getImageUrl(imagePath);

        return new BreadLoadListDto(
                tuple.get("id", Long.class),
                tuple.get("name", String.class),
                tuple.get("price", Integer.class),
                tuple.get("stock", Integer.class),
                imageUrl,
                popularityScore
        );
    }


    public String getImageUrl(String imagePath) {
        // 웹 서버의 도메인 또는 IP 주소
        String baseUrl = "http://52.79.172.135:8080";

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

    public Bread convertToBread(BreadRegisterDto breadRegisterDto){
        return new Bread(breadRegisterDto.getName(), breadRegisterDto.getPrice(), breadRegisterDto.getStock());
    }

    public BreadLoadListDto convertToBreadDto(Bread bread){
        String imageUrl = getImageUrl(bread.getImagePath());
        return new BreadLoadListDto(bread.getId(),bread.getName(), bread.getPrice(),bread.getStock(),imageUrl);
    }

//    public List<BreadRegisterDto> convertToDtoList(List<Bread> breadList) {
//        List<BreadRegisterDto> breadRegisterDtoList = new ArrayList<>();
//        for (Bread bread : breadList) {
//            breadRegisterDtoList.add(convertToBreadDto(bread));
//        }
//        return breadRegisterDtoList;
//    }


}
