package test.bbang.service;

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
        dto.setImageUrl(bread.getImageUrl());
        return dto;
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

