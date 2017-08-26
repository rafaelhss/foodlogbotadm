package com.foodlog.repository;

import com.foodlog.domain.User;
import com.foodlog.domain.UserTelegram;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the UserTelegram entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserTelegramRepository extends JpaRepository<UserTelegram,Long> {

    UserTelegram findOneByTelegramId(Integer id);
}
