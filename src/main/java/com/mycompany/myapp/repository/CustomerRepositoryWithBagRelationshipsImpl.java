package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Customer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class CustomerRepositoryWithBagRelationshipsImpl implements CustomerRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Customer> fetchBagRelationships(Optional<Customer> customer) {
        return customer.map(this::fetchFollowers);
    }

    @Override
    public Page<Customer> fetchBagRelationships(Page<Customer> customers) {
        return new PageImpl<>(fetchBagRelationships(customers.getContent()), customers.getPageable(), customers.getTotalElements());
    }

    @Override
    public List<Customer> fetchBagRelationships(List<Customer> customers) {
        return Optional.of(customers).map(this::fetchFollowers).orElse(Collections.emptyList());
    }

    Customer fetchFollowers(Customer result) {
        return entityManager
            .createQuery(
                "select customer from Customer customer left join fetch customer.followers where customer is :customer",
                Customer.class
            )
            .setParameter("customer", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Customer> fetchFollowers(List<Customer> customers) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, customers.size()).forEach(index -> order.put(customers.get(index).getId(), index));
        List<Customer> result = entityManager
            .createQuery(
                "select distinct customer from Customer customer left join fetch customer.followers where customer in :customers",
                Customer.class
            )
            .setParameter("customers", customers)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
