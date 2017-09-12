package com.foodlog.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.foodlog.domain.Jaca;

import com.foodlog.repository.JacaRepository;
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
 * REST controller for managing Jaca.
 */
@RestController
@RequestMapping("/api")
public class JacaResource {

    private final Logger log = LoggerFactory.getLogger(JacaResource.class);

    private static final String ENTITY_NAME = "jaca";

    private final JacaRepository jacaRepository;

    public JacaResource(JacaRepository jacaRepository) {
        this.jacaRepository = jacaRepository;
    }

    /**
     * POST  /jacas : Create a new jaca.
     *
     * @param jaca the jaca to create
     * @return the ResponseEntity with status 201 (Created) and with body the new jaca, or with status 400 (Bad Request) if the jaca has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/jacas")
    @Timed
    public ResponseEntity<Jaca> createJaca(@Valid @RequestBody Jaca jaca) throws URISyntaxException {
        log.debug("REST request to save Jaca : {}", jaca);
        if (jaca.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new jaca cannot already have an ID")).body(null);
        }
        Jaca result = jacaRepository.save(jaca);
        return ResponseEntity.created(new URI("/api/jacas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /jacas : Updates an existing jaca.
     *
     * @param jaca the jaca to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated jaca,
     * or with status 400 (Bad Request) if the jaca is not valid,
     * or with status 500 (Internal Server Error) if the jaca couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/jacas")
    @Timed
    public ResponseEntity<Jaca> updateJaca(@Valid @RequestBody Jaca jaca) throws URISyntaxException {
        log.debug("REST request to update Jaca : {}", jaca);
        if (jaca.getId() == null) {
            return createJaca(jaca);
        }
        Jaca result = jacaRepository.save(jaca);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, jaca.getId().toString()))
            .body(result);
    }

    /**
     * GET  /jacas : get all the jacas.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of jacas in body
     */
    @GetMapping("/jacas")
    @Timed
    public List<Jaca> getAllJacas() {
        log.debug("REST request to get all Jacas");
        return jacaRepository.findAll();
    }

    /**
     * GET  /jacas/:id : get the "id" jaca.
     *
     * @param id the id of the jaca to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the jaca, or with status 404 (Not Found)
     */
    @GetMapping("/jacas/{id}")
    @Timed
    public ResponseEntity<Jaca> getJaca(@PathVariable Long id) {
        log.debug("REST request to get Jaca : {}", id);
        Jaca jaca = jacaRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(jaca));
    }

    /**
     * DELETE  /jacas/:id : delete the "id" jaca.
     *
     * @param id the id of the jaca to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/jacas/{id}")
    @Timed
    public ResponseEntity<Void> deleteJaca(@PathVariable Long id) {
        log.debug("REST request to delete Jaca : {}", id);
        jacaRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
