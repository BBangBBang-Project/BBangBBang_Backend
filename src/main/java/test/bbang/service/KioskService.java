package test.bbang.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.bbang.Dto.Bread.BreadPurchaseDto;
import test.bbang.Dto.Bread.BreadRegisterDto;
import test.bbang.Dto.Order.OrderDto;
import test.bbang.Entity.Bread;
import test.bbang.repository.BreadRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class KioskService {
    private final BreadRepository breadRepository;

    public KioskService(BreadRepository breadRepository) {
        this.breadRepository = breadRepository;
    }

    public List<Bread> findAllBreads() {
        return breadRepository.findAll();
    }

    @Transactional
    public boolean saveBread(BreadRegisterDto breadRegisterDto){

        try{
            Optional<Bread> optionalBread = findByName(breadRegisterDto.getName());

            Bread bread = optionalBread
                    .map(existingBread -> {
                        existingBread.setStock(breadRegisterDto.getStock() + existingBread.getStock());
                        return existingBread;
                    })
                    .orElseGet(() -> convertToBread(breadRegisterDto));
            breadRepository.save(bread);
            return true;
        }
        catch (Exception e){
            log.error("saveBread 오류",e);
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


    @Transactional
    public boolean orderCheck(OrderDto orderDto) {
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


}
