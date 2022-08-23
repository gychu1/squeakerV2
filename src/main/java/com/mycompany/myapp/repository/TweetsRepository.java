package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Tweets;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Tweets entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TweetsRepository extends JpaRepository<Tweets, Long> {}
