package test.bbang;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import test.bbang.Dto.Bread.BreadRegisterDto;
import test.bbang.Entity.Bread;
import test.bbang.repository.TestRepository;

@SpringBootTest
class BbangApplicationTests {

	TestRepository testRepository = new TestRepository();

	@AfterEach
	void setup() {
		testRepository.clearStore();
	}

	@Test
	void addBread(){

		BreadRegisterDto breadRegisterDto = new BreadRegisterDto("식빵",3000 ,5);

		Bread bread = new Bread(breadRegisterDto.getName(), breadRegisterDto.getPrice(), breadRegisterDto.getStock());


		testRepository.save(bread);


		Bread tmp = testRepository.findByName("식빵");
		Assertions.assertThat(tmp.getStock()).isEqualTo(5);

	}

}
