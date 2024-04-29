package test.bbang.repository;

import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import test.bbang.Dto.Bread.BreadLoadListDto;
import test.bbang.Entity.Bread;

import java.util.List;
import java.util.Optional;

@Repository
public interface BreadRepository extends JpaRepository<Bread,Long> {
    Optional<Bread> findByName(String name);

    //빵의 인기도를 계산해서 결과를 내림차순으로 정렬해 반환.
    @Query(value = "SELECT b.id as id, b.name as name, b.price as price, b.stock as stock, b.image_path AS imageUrl, (COUNT(fi.id) + COALESCE(SUM(oi.quantity), 0)) AS popularityScore " +
            "FROM bread b " +
            "LEFT JOIN favorite_item fi ON b.id = fi.bread_id " +
            "LEFT JOIN order_item oi ON b.id = oi.bread_id " +
            "GROUP BY b.id " +
            "ORDER BY popularityScore DESC", nativeQuery = true)
    List<Tuple> findBreadPopularityNative();


}
