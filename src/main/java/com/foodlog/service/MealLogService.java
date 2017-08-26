package com.foodlog.service;

import com.foodlog.domain.MealLog;
import com.foodlog.repository.MealLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing MealLog.
 */
@Service
@Transactional
public class MealLogService {

    private final Logger log = LoggerFactory.getLogger(MealLogService.class);

    private final MealLogRepository mealLogRepository;

    public MealLogService(MealLogRepository mealLogRepository) {
        this.mealLogRepository = mealLogRepository;
    }

    /**
     * Save a mealLog.
     *
     * @param mealLog the entity to save
     * @return the persisted entity
     */
    public MealLog save(MealLog mealLog) {
        log.debug("Request to save MealLog : {}", mealLog);
        return mealLogRepository.save(mealLog);
    }

    /**
     *  Get all the mealLogs.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<MealLog> findAll(Pageable pageable) {
        log.debug("Request to get all MealLogs");
        return mealLogRepository.findByUserIsCurrentUser(pageable);
    }

    /**
     *  Get one mealLog by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public MealLog findOne(Long id) {
        log.debug("Request to get MealLog : {}", id);
        return mealLogRepository.findOne(id);
    }

    /**
     *  Delete the  mealLog by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete MealLog : {}", id);
        mealLogRepository.delete(id);
    }
}
