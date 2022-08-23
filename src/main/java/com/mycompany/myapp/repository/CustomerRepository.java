package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Customer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Customer entity.
 *
 * When extending this class, extend CustomerRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface CustomerRepository extends CustomerRepositoryWithBagRelationships, JpaRepository<Customer, Long> {
    @Query("select customer from Customer customer where customer.user.login = ?#{principal.username}")
    List<Customer> findByUserIsCurrentUser();

    default Optional<Customer> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Customer> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Customer> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select distinct customer from Customer customer left join fetch customer.user",
        countQuery = "select count(distinct customer) from Customer customer"
    )
    Page<Customer> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct customer from Customer customer left join fetch customer.user")
    List<Customer> findAllWithToOneRelationships();

    @Query("select customer from Customer customer left join fetch customer.user where customer.id =:id")
    Optional<Customer> findOneWithToOneRelationships(@Param("id") Long id);
}
