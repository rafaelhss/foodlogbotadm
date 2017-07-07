package com.foodlog.repository;

import com.foodlog.domain.MealLog;
import com.foodlog.domain.Weight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Weight entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WeightRepository extends JpaRepository<Weight,Long> {
    Page<Weight> findByOrderByWeightDateTimeDesc(Pageable pageable);
}
