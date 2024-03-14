package test.bbang.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.bbang.Entity.Bread;

import java.util.Optional;

@Repository
public interface BreadRepository extends JpaRepository<Bread,Long> {
    Optional<Bread> findByName(String name);
}
