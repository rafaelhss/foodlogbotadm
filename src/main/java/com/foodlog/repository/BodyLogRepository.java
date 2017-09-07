package com.foodlog.repository;

import com.foodlog.domain.BodyLog;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import java.util.List;

/**
 * Spring Data JPA repository for the BodyLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BodyLogRepository extends JpaRepository<BodyLog,Long> {

    @Query("select body_log from BodyLog body_log where body_log.user.login = ?#{principal.username}")
    List<BodyLog> findByUserIsCurrentUser();
    
}
