package test.bbang.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.bbang.Entity.Customer;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUsernameAndPassword(String username, String password);
    Optional<Customer> findByQuickPassword(String quickPassword);
}

