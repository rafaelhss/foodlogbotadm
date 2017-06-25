package com.foodlog.repository;

import com.foodlog.domain.ScheduledMeal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the ScheduledMeal entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScheduledMealRepository extends JpaRepository<ScheduledMeal,Long> {
    public List<ScheduledMeal> findByName(String name);
    List<ScheduledMeal> findByOrderByTargetTime();
    List<ScheduledMeal> findByOrderByTargetTimeDesc();

}
