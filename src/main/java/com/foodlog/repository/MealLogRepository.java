package com.foodlog.repository;

import com.foodlog.domain.MealLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.Date;
import java.util.List;


/**
 * Spring Data JPA repository for the MealLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MealLogRepository extends JpaRepository<MealLog,Long> {
    Page<MealLog> findByOrderByMealDateTimeDesc(Pageable pageable);
    List<MealLog> findByMealDateTimeAfterOrderByMealDateTimeDesc(Date today);
}
