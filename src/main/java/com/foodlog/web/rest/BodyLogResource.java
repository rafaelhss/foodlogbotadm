package com.foodlog.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.foodlog.domain.BodyLog;

import com.foodlog.repository.BodyLogRepository;
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
 * REST controller for managing BodyLog.
 */
@RestController
@RequestMapping("/api")
public class BodyLogResource {

    private final Logger log = LoggerFactory.getLogger(BodyLogResource.class);

    private static final String ENTITY_NAME = "bodyLog";

    private final BodyLogRepository bodyLogRepository;

    public BodyLogResource(BodyLogRepository bodyLogRepository) {
        this.bodyLogRepository = bodyLogRepository;
    }

    /**
     * POST  /body-logs : Create a new bodyLog.
     *
     * @param bodyLog the bodyLog to create
     * @return the ResponseEntity with status 201 (Created) and with body the new bodyLog, or with status 400 (Bad Request) if the bodyLog has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/body-logs")
    @Timed
    public ResponseEntity<BodyLog> createBodyLog(@Valid @RequestBody BodyLog bodyLog) throws URISyntaxException {
        log.debug("REST request to save BodyLog : {}", bodyLog);
        if (bodyLog.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new bodyLog cannot already have an ID")).body(null);
        }
        BodyLog result = bodyLogRepository.save(bodyLog);
        return ResponseEntity.created(new URI("/api/body-logs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /body-logs : Updates an existing bodyLog.
     *
     * @param bodyLog the bodyLog to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated bodyLog,
     * or with status 400 (Bad Request) if the bodyLog is not valid,
     * or with status 500 (Internal Server Error) if the bodyLog couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/body-logs")
    @Timed
    public ResponseEntity<BodyLog> updateBodyLog(@Valid @RequestBody BodyLog bodyLog) throws URISyntaxException {
        log.debug("REST request to update BodyLog : {}", bodyLog);
        if (bodyLog.getId() == null) {
            return createBodyLog(bodyLog);
        }
        BodyLog result = bodyLogRepository.save(bodyLog);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, bodyLog.getId().toString()))
            .body(result);
    }

    /**
     * GET  /body-logs : get all the bodyLogs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of bodyLogs in body
     */
    @GetMapping("/body-logs")
    @Timed
    public ResponseEntity<List<BodyLog>> getAllBodyLogs(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of BodyLogs");
        Page<BodyLog> page = bodyLogRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/body-logs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /body-logs/:id : get the "id" bodyLog.
     *
     * @param id the id of the bodyLog to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the bodyLog, or with status 404 (Not Found)
     */
    @GetMapping("/body-logs/{id}")
    @Timed
    public ResponseEntity<BodyLog> getBodyLog(@PathVariable Long id) {
        log.debug("REST request to get BodyLog : {}", id);
        BodyLog bodyLog = bodyLogRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(bodyLog));
    }

    /**
     * DELETE  /body-logs/:id : delete the "id" bodyLog.
     *
     * @param id the id of the bodyLog to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/body-logs/{id}")
    @Timed
    public ResponseEntity<Void> deleteBodyLog(@PathVariable Long id) {
        log.debug("REST request to delete BodyLog : {}", id);
        bodyLogRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
