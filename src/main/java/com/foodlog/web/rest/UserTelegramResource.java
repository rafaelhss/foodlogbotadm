package com.foodlog.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.foodlog.domain.UserTelegram;

import com.foodlog.repository.UserTelegramRepository;
import com.foodlog.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing UserTelegram.
 */
@RestController
@RequestMapping("/api")
public class UserTelegramResource {

    private final Logger log = LoggerFactory.getLogger(UserTelegramResource.class);

    private static final String ENTITY_NAME = "userTelegram";

    private final UserTelegramRepository userTelegramRepository;

    public UserTelegramResource(UserTelegramRepository userTelegramRepository) {
        this.userTelegramRepository = userTelegramRepository;
    }

    /**
     * POST  /user-telegrams : Create a new userTelegram.
     *
     * @param userTelegram the userTelegram to create
     * @return the ResponseEntity with status 201 (Created) and with body the new userTelegram, or with status 400 (Bad Request) if the userTelegram has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/user-telegrams")
    @Timed
    public ResponseEntity<UserTelegram> createUserTelegram(@RequestBody UserTelegram userTelegram) throws URISyntaxException {
        log.debug("REST request to save UserTelegram : {}", userTelegram);
        if (userTelegram.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new userTelegram cannot already have an ID")).body(null);
        }
        UserTelegram result = userTelegramRepository.save(userTelegram);
        return ResponseEntity.created(new URI("/api/user-telegrams/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /user-telegrams : Updates an existing userTelegram.
     *
     * @param userTelegram the userTelegram to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated userTelegram,
     * or with status 400 (Bad Request) if the userTelegram is not valid,
     * or with status 500 (Internal Server Error) if the userTelegram couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/user-telegrams")
    @Timed
    public ResponseEntity<UserTelegram> updateUserTelegram(@RequestBody UserTelegram userTelegram) throws URISyntaxException {
        log.debug("REST request to update UserTelegram : {}", userTelegram);
        if (userTelegram.getId() == null) {
            return createUserTelegram(userTelegram);
        }
        UserTelegram result = userTelegramRepository.save(userTelegram);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, userTelegram.getId().toString()))
            .body(result);
    }

    /**
     * GET  /user-telegrams : get all the userTelegrams.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of userTelegrams in body
     */
    @GetMapping("/user-telegrams")
    @Timed
    public List<UserTelegram> getAllUserTelegrams() {
        log.debug("REST request to get all UserTelegrams");
        return userTelegramRepository.findAll();
    }

    /**
     * GET  /user-telegrams/:id : get the "id" userTelegram.
     *
     * @param id the id of the userTelegram to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the userTelegram, or with status 404 (Not Found)
     */
    @GetMapping("/user-telegrams/{id}")
    @Timed
    public ResponseEntity<UserTelegram> getUserTelegram(@PathVariable Long id) {
        log.debug("REST request to get UserTelegram : {}", id);
        UserTelegram userTelegram = userTelegramRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(userTelegram));
    }

    /**
     * DELETE  /user-telegrams/:id : delete the "id" userTelegram.
     *
     * @param id the id of the userTelegram to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/user-telegrams/{id}")
    @Timed
    public ResponseEntity<Void> deleteUserTelegram(@PathVariable Long id) {
        log.debug("REST request to delete UserTelegram : {}", id);
        userTelegramRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
