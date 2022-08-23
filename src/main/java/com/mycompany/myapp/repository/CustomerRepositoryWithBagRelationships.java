package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Customer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface CustomerRepositoryWithBagRelationships {
    Optional<Customer> fetchBagRelationships(Optional<Customer> customer);

    List<Customer> fetchBagRelationships(List<Customer> customers);

    Page<Customer> fetchBagRelationships(Page<Customer> customers);
}
