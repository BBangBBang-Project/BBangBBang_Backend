package test.bbang.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.bbang.Dto.Bread.BreadLoadListDto;
import test.bbang.Entity.Bread;
import test.bbang.repository.BreadRepository;

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
        //dto.setImageUrl(bread.getImageUrl());
        return dto;
    }

}

