package test.bbang.service;

import jakarta.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.bbang.Dto.Bread.BreadLoadListDto;
import test.bbang.Dto.Bread.BreadRegisterDto;
import test.bbang.Entity.Bread;
import test.bbang.repository.BreadRepository;

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

    private BreadLoadListDto convertToDto(Bread bread) {
        BreadLoadListDto dto = new BreadLoadListDto();
        dto.setId(bread.getId());
        dto.setName(bread.getName());
        dto.setPrice((double) bread.getPrice());
        dto.setStock(bread.getStock());
        log.debug("Converting bread entity to DTO. Bread imagePath: {}", bread.getImagePath()); // 로그 추가
        String imageUrl = getImageUrl(bread.getImagePath());
        dto.setImageUrl(imageUrl);
        log.debug("Image URL for bread {}: {}", bread.getId(), imageUrl); // 로그 추가
        return dto;
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

    public Bread convertToBread(BreadRegisterDto breadRegisterDto){
        return new Bread(breadRegisterDto.getName(), breadRegisterDto.getPrice(), breadRegisterDto.getStock());
    }

    public BreadRegisterDto convertToBreadDto(Bread bread){
        return new BreadRegisterDto(bread.getId(),bread.getName(),bread.getPrice(),bread.getStock());
    }

    public List<BreadRegisterDto> convertToDtoList(List<Bread> breadList) {
        List<BreadRegisterDto> breadRegisterDtoList = new ArrayList<>();
        for (Bread bread : breadList) {
            breadRegisterDtoList.add(convertToBreadDto(bread));
        }
        return breadRegisterDtoList;
    }


}
