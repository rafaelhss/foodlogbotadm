package com.foodlog.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.foodlog.domain.ScheduledMeal;

import com.foodlog.repository.ScheduledMealRepository;
import com.foodlog.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing ScheduledMeal.
 */
@RestController
@RequestMapping("/api")
public class ScheduledMealResource {

    private final Logger log = LoggerFactory.getLogger(ScheduledMealResource.class);

    private static final String ENTITY_NAME = "scheduledMeal";

    private final ScheduledMealRepository scheduledMealRepository;

    public ScheduledMealResource(ScheduledMealRepository scheduledMealRepository) {
        this.scheduledMealRepository = scheduledMealRepository;
    }

    /**
     * POST  /scheduled-meals : Create a new scheduledMeal.
     *
     * @param scheduledMeal the scheduledMeal to create
     * @return the ResponseEntity with status 201 (Created) and with body the new scheduledMeal, or with status 400 (Bad Request) if the scheduledMeal has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/scheduled-meals")
    @Timed
    public ResponseEntity<ScheduledMeal> createScheduledMeal(@Valid @RequestBody ScheduledMeal scheduledMeal) throws URISyntaxException {
        log.debug("REST request to save ScheduledMeal : {}", scheduledMeal);
        if (scheduledMeal.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new scheduledMeal cannot already have an ID")).body(null);
        }
        ScheduledMeal result = scheduledMealRepository.save(scheduledMeal);
        return ResponseEntity.created(new URI("/api/scheduled-meals/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /scheduled-meals : Updates an existing scheduledMeal.
     *
     * @param scheduledMeal the scheduledMeal to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated scheduledMeal,
     * or with status 400 (Bad Request) if the scheduledMeal is not valid,
     * or with status 500 (Internal Server Error) if the scheduledMeal couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/scheduled-meals")
    @Timed
    public ResponseEntity<ScheduledMeal> updateScheduledMeal(@Valid @RequestBody ScheduledMeal scheduledMeal) throws URISyntaxException {
        log.debug("REST request to update ScheduledMeal : {}", scheduledMeal);
        if (scheduledMeal.getId() == null) {
            return createScheduledMeal(scheduledMeal);
        }
        ScheduledMeal result = scheduledMealRepository.save(scheduledMeal);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, scheduledMeal.getId().toString()))
            .body(result);
    }

    /**
     * GET  /scheduled-meals : get all the scheduledMeals.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of scheduledMeals in body
     */
    @GetMapping("/scheduled-meals")
    @Timed
    public List<ScheduledMeal> getAllScheduledMeals() {
        log.debug("REST request to get all ScheduledMeals");
        return scheduledMealRepository.findAll();
    }

    /**
     * GET  /scheduled-meals/:id : get the "id" scheduledMeal.
     *
     * @param id the id of the scheduledMeal to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the scheduledMeal, or with status 404 (Not Found)
     */
    @GetMapping("/scheduled-meals/{id}")
    @Timed
    public ResponseEntity<ScheduledMeal> getScheduledMeal(@PathVariable Long id) {
        log.debug("REST request to get ScheduledMeal : {}", id);
        ScheduledMeal scheduledMeal = scheduledMealRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(scheduledMeal));
    }

    /**
     * DELETE  /scheduled-meals/:id : delete the "id" scheduledMeal.
     *
     * @param id the id of the scheduledMeal to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/scheduled-meals/{id}")
    @Timed
    public ResponseEntity<Void> deleteScheduledMeal(@PathVariable Long id) {
        log.debug("REST request to delete ScheduledMeal : {}", id);
        scheduledMealRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
