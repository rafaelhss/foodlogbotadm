package com.foodlog.repository;

import com.foodlog.domain.Jaca;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import java.util.List;

/**
 * Spring Data JPA repository for the Jaca entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JacaRepository extends JpaRepository<Jaca,Long> {

    @Query("select jaca from Jaca jaca where jaca.user.login = ?#{principal.username}")
    List<Jaca> findByUserIsCurrentUser();
    
}
