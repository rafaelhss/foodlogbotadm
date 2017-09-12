package com.foodlog.web.rest;

import com.foodlog.FoodlogbotadmApp;

import com.foodlog.domain.Jaca;
import com.foodlog.domain.User;
import com.foodlog.repository.JacaRepository;
import com.foodlog.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the JacaResource REST controller.
 *
 * @see JacaResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodlogbotadmApp.class)
public class JacaResourceIntTest {

    private static final Instant DEFAULT_JACA_DATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_JACA_DATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private JacaRepository jacaRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restJacaMockMvc;

    private Jaca jaca;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        JacaResource jacaResource = new JacaResource(jacaRepository);
        this.restJacaMockMvc = MockMvcBuilders.standaloneSetup(jacaResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Jaca createEntity(EntityManager em) {
        Jaca jaca = new Jaca()
            .jacaDateTime(DEFAULT_JACA_DATE_TIME);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        jaca.setUser(user);
        return jaca;
    }

    @Before
    public void initTest() {
        jaca = createEntity(em);
    }

    @Test
    @Transactional
    public void createJaca() throws Exception {
        int databaseSizeBeforeCreate = jacaRepository.findAll().size();

        // Create the Jaca
        restJacaMockMvc.perform(post("/api/jacas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jaca)))
            .andExpect(status().isCreated());

        // Validate the Jaca in the database
        List<Jaca> jacaList = jacaRepository.findAll();
        assertThat(jacaList).hasSize(databaseSizeBeforeCreate + 1);
        Jaca testJaca = jacaList.get(jacaList.size() - 1);
        assertThat(testJaca.getJacaDateTime()).isEqualTo(DEFAULT_JACA_DATE_TIME);
    }

    @Test
    @Transactional
    public void createJacaWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = jacaRepository.findAll().size();

        // Create the Jaca with an existing ID
        jaca.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restJacaMockMvc.perform(post("/api/jacas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jaca)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Jaca> jacaList = jacaRepository.findAll();
        assertThat(jacaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkJacaDateTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = jacaRepository.findAll().size();
        // set the field null
        jaca.setJacaDateTime(null);

        // Create the Jaca, which fails.

        restJacaMockMvc.perform(post("/api/jacas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jaca)))
            .andExpect(status().isBadRequest());

        List<Jaca> jacaList = jacaRepository.findAll();
        assertThat(jacaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllJacas() throws Exception {
        // Initialize the database
        jacaRepository.saveAndFlush(jaca);

        // Get all the jacaList
        restJacaMockMvc.perform(get("/api/jacas?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jaca.getId().intValue())))
            .andExpect(jsonPath("$.[*].jacaDateTime").value(hasItem(DEFAULT_JACA_DATE_TIME.toString())));
    }

    @Test
    @Transactional
    public void getJaca() throws Exception {
        // Initialize the database
        jacaRepository.saveAndFlush(jaca);

        // Get the jaca
        restJacaMockMvc.perform(get("/api/jacas/{id}", jaca.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(jaca.getId().intValue()))
            .andExpect(jsonPath("$.jacaDateTime").value(DEFAULT_JACA_DATE_TIME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingJaca() throws Exception {
        // Get the jaca
        restJacaMockMvc.perform(get("/api/jacas/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateJaca() throws Exception {
        // Initialize the database
        jacaRepository.saveAndFlush(jaca);
        int databaseSizeBeforeUpdate = jacaRepository.findAll().size();

        // Update the jaca
        Jaca updatedJaca = jacaRepository.findOne(jaca.getId());
        updatedJaca
            .jacaDateTime(UPDATED_JACA_DATE_TIME);

        restJacaMockMvc.perform(put("/api/jacas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedJaca)))
            .andExpect(status().isOk());

        // Validate the Jaca in the database
        List<Jaca> jacaList = jacaRepository.findAll();
        assertThat(jacaList).hasSize(databaseSizeBeforeUpdate);
        Jaca testJaca = jacaList.get(jacaList.size() - 1);
        assertThat(testJaca.getJacaDateTime()).isEqualTo(UPDATED_JACA_DATE_TIME);
    }

    @Test
    @Transactional
    public void updateNonExistingJaca() throws Exception {
        int databaseSizeBeforeUpdate = jacaRepository.findAll().size();

        // Create the Jaca

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restJacaMockMvc.perform(put("/api/jacas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jaca)))
            .andExpect(status().isCreated());

        // Validate the Jaca in the database
        List<Jaca> jacaList = jacaRepository.findAll();
        assertThat(jacaList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteJaca() throws Exception {
        // Initialize the database
        jacaRepository.saveAndFlush(jaca);
        int databaseSizeBeforeDelete = jacaRepository.findAll().size();

        // Get the jaca
        restJacaMockMvc.perform(delete("/api/jacas/{id}", jaca.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Jaca> jacaList = jacaRepository.findAll();
        assertThat(jacaList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Jaca.class);
        Jaca jaca1 = new Jaca();
        jaca1.setId(1L);
        Jaca jaca2 = new Jaca();
        jaca2.setId(jaca1.getId());
        assertThat(jaca1).isEqualTo(jaca2);
        jaca2.setId(2L);
        assertThat(jaca1).isNotEqualTo(jaca2);
        jaca1.setId(null);
        assertThat(jaca1).isNotEqualTo(jaca2);
    }
}
