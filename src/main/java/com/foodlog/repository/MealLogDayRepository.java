package com.foodlog.repository;

import com.foodlog.domain.MealLogDay;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the MealLogDay entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MealLogDayRepository extends JpaRepository<MealLogDay,Long> {
    
}
