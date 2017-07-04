package com.foodlog.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.foodlog.domain.MealLog;
import com.foodlog.service.MealLogService;
import com.foodlog.web.rest.util.HeaderUtil;
import com.foodlog.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing MealLog.
 */
@RestController
@RequestMapping("/api")
public class MealLogResource {

    private final Logger log = LoggerFactory.getLogger(MealLogResource.class);

    private static final String ENTITY_NAME = "mealLog";

    private final MealLogService mealLogService;

    public MealLogResource(MealLogService mealLogService) {
        this.mealLogService = mealLogService;
    }

    /**
     * POST  /meal-logs : Create a new mealLog.
     *
     * @param mealLog the mealLog to create
     * @return the ResponseEntity with status 201 (Created) and with body the new mealLog, or with status 400 (Bad Request) if the mealLog has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/meal-logs")
    @Timed
    public ResponseEntity<MealLog> createMealLog(@Valid @RequestBody MealLog mealLog) throws URISyntaxException {
        log.debug("REST request to save MealLog : {}", mealLog);
        if (mealLog.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new mealLog cannot already have an ID")).body(null);
        }
        MealLog result = mealLogService.save(mealLog);
        return ResponseEntity.created(new URI("/api/meal-logs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /meal-logs : Updates an existing mealLog.
     *
     * @param mealLog the mealLog to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated mealLog,
     * or with status 400 (Bad Request) if the mealLog is not valid,
     * or with status 500 (Internal Server Error) if the mealLog couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/meal-logs")
    @Timed
    public ResponseEntity<MealLog> updateMealLog(@Valid @RequestBody MealLog mealLog) throws URISyntaxException {
        log.debug("REST request to update MealLog : {}", mealLog);
        if (mealLog.getId() == null) {
            return createMealLog(mealLog);
        }
        MealLog result = mealLogService.save(mealLog);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, mealLog.getId().toString()))
            .body(result);
    }

    /**
     * GET  /meal-logs : get all the mealLogs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of mealLogs in body
     */
    @GetMapping("/meal-logs")
    @Timed
    public ResponseEntity<List<MealLog>> getAllMealLogs(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of MealLogs");
        Page<MealLog> page = mealLogService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/meal-logs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /meal-logs/:id : get the "id" mealLog.
     *
     * @param id the id of the mealLog to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the mealLog, or with status 404 (Not Found)
     */
    @GetMapping("/meal-logs/{id}")
    @Timed
    public ResponseEntity<MealLog> getMealLog(@PathVariable Long id) {
        log.debug("REST request to get MealLog : {}", id);
        MealLog mealLog = mealLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(mealLog));
    }

    /**
     * DELETE  /meal-logs/:id : delete the "id" mealLog.
     *
     * @param id the id of the mealLog to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/meal-logs/{id}")
    @Timed
    public ResponseEntity<Void> deleteMealLog(@PathVariable Long id) {
        log.debug("REST request to delete MealLog : {}", id);
        mealLogService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
