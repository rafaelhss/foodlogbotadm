package com.foodlog.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.foodlog.domain.MealLogDay;

import com.foodlog.repository.MealLogDayRepository;
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

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing MealLogDay.
 */
@RestController
@RequestMapping("/api")
public class MealLogDayResource {

    private final Logger log = LoggerFactory.getLogger(MealLogDayResource.class);

    private static final String ENTITY_NAME = "mealLogDay";

    private final MealLogDayRepository mealLogDayRepository;

    public MealLogDayResource(MealLogDayRepository mealLogDayRepository) {
        this.mealLogDayRepository = mealLogDayRepository;
    }

    /**
     * POST  /meal-log-days : Create a new mealLogDay.
     *
     * @param mealLogDay the mealLogDay to create
     * @return the ResponseEntity with status 201 (Created) and with body the new mealLogDay, or with status 400 (Bad Request) if the mealLogDay has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/meal-log-days")
    @Timed
    public ResponseEntity<MealLogDay> createMealLogDay(@RequestBody MealLogDay mealLogDay) throws URISyntaxException {
        log.debug("REST request to save MealLogDay : {}", mealLogDay);
        if (mealLogDay.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new mealLogDay cannot already have an ID")).body(null);
        }
        MealLogDay result = mealLogDayRepository.save(mealLogDay);
        return ResponseEntity.created(new URI("/api/meal-log-days/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /meal-log-days : Updates an existing mealLogDay.
     *
     * @param mealLogDay the mealLogDay to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated mealLogDay,
     * or with status 400 (Bad Request) if the mealLogDay is not valid,
     * or with status 500 (Internal Server Error) if the mealLogDay couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/meal-log-days")
    @Timed
    public ResponseEntity<MealLogDay> updateMealLogDay(@RequestBody MealLogDay mealLogDay) throws URISyntaxException {
        log.debug("REST request to update MealLogDay : {}", mealLogDay);
        if (mealLogDay.getId() == null) {
            return createMealLogDay(mealLogDay);
        }
        MealLogDay result = mealLogDayRepository.save(mealLogDay);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, mealLogDay.getId().toString()))
            .body(result);
    }

    /**
     * GET  /meal-log-days : get all the mealLogDays.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of mealLogDays in body
     */
    @GetMapping("/meal-log-days")
    @Timed
    public ResponseEntity<List<MealLogDay>> getAllMealLogDays(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of MealLogDays");
        Page<MealLogDay> page = mealLogDayRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/meal-log-days");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /meal-log-days/:id : get the "id" mealLogDay.
     *
     * @param id the id of the mealLogDay to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the mealLogDay, or with status 404 (Not Found)
     */
    @GetMapping("/meal-log-days/{id}")
    @Timed
    public ResponseEntity<MealLogDay> getMealLogDay(@PathVariable Long id) {
        log.debug("REST request to get MealLogDay : {}", id);
        MealLogDay mealLogDay = mealLogDayRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(mealLogDay));
    }

    /**
     * DELETE  /meal-log-days/:id : delete the "id" mealLogDay.
     *
     * @param id the id of the mealLogDay to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/meal-log-days/{id}")
    @Timed
    public ResponseEntity<Void> deleteMealLogDay(@PathVariable Long id) {
        log.debug("REST request to delete MealLogDay : {}", id);
        mealLogDayRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
