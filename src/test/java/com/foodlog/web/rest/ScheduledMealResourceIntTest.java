package com.foodlog.web.rest;

import com.foodlog.FoodlogbotadmApp;

import com.foodlog.domain.ScheduledMeal;
import com.foodlog.repository.ScheduledMealRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ScheduledMealResource REST controller.
 *
 * @see ScheduledMealResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodlogbotadmApp.class)
public class ScheduledMealResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_TARGET_TIME = "19:05";
    private static final String UPDATED_TARGET_TIME = "04:29";

    @Autowired
    private ScheduledMealRepository scheduledMealRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restScheduledMealMockMvc;

    private ScheduledMeal scheduledMeal;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ScheduledMealResource scheduledMealResource = new ScheduledMealResource(scheduledMealRepository);
        this.restScheduledMealMockMvc = MockMvcBuilders.standaloneSetup(scheduledMealResource)
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
    public static ScheduledMeal createEntity(EntityManager em) {
        ScheduledMeal scheduledMeal = new ScheduledMeal()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .targetTime(DEFAULT_TARGET_TIME);
        return scheduledMeal;
    }

    @Before
    public void initTest() {
        scheduledMeal = createEntity(em);
    }

    @Test
    @Transactional
    public void createScheduledMeal() throws Exception {
        int databaseSizeBeforeCreate = scheduledMealRepository.findAll().size();

        // Create the ScheduledMeal
        restScheduledMealMockMvc.perform(post("/api/scheduled-meals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduledMeal)))
            .andExpect(status().isCreated());

        // Validate the ScheduledMeal in the database
        List<ScheduledMeal> scheduledMealList = scheduledMealRepository.findAll();
        assertThat(scheduledMealList).hasSize(databaseSizeBeforeCreate + 1);
        ScheduledMeal testScheduledMeal = scheduledMealList.get(scheduledMealList.size() - 1);
        assertThat(testScheduledMeal.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testScheduledMeal.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testScheduledMeal.getTargetTime()).isEqualTo(DEFAULT_TARGET_TIME);
    }

    @Test
    @Transactional
    public void createScheduledMealWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = scheduledMealRepository.findAll().size();

        // Create the ScheduledMeal with an existing ID
        scheduledMeal.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restScheduledMealMockMvc.perform(post("/api/scheduled-meals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduledMeal)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<ScheduledMeal> scheduledMealList = scheduledMealRepository.findAll();
        assertThat(scheduledMealList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduledMealRepository.findAll().size();
        // set the field null
        scheduledMeal.setName(null);

        // Create the ScheduledMeal, which fails.

        restScheduledMealMockMvc.perform(post("/api/scheduled-meals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduledMeal)))
            .andExpect(status().isBadRequest());

        List<ScheduledMeal> scheduledMealList = scheduledMealRepository.findAll();
        assertThat(scheduledMealList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduledMealRepository.findAll().size();
        // set the field null
        scheduledMeal.setDescription(null);

        // Create the ScheduledMeal, which fails.

        restScheduledMealMockMvc.perform(post("/api/scheduled-meals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduledMeal)))
            .andExpect(status().isBadRequest());

        List<ScheduledMeal> scheduledMealList = scheduledMealRepository.findAll();
        assertThat(scheduledMealList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTargetTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduledMealRepository.findAll().size();
        // set the field null
        scheduledMeal.setTargetTime(null);

        // Create the ScheduledMeal, which fails.

        restScheduledMealMockMvc.perform(post("/api/scheduled-meals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduledMeal)))
            .andExpect(status().isBadRequest());

        List<ScheduledMeal> scheduledMealList = scheduledMealRepository.findAll();
        assertThat(scheduledMealList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllScheduledMeals() throws Exception {
        // Initialize the database
        scheduledMealRepository.saveAndFlush(scheduledMeal);

        // Get all the scheduledMealList
        restScheduledMealMockMvc.perform(get("/api/scheduled-meals?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scheduledMeal.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].targetTime").value(hasItem(DEFAULT_TARGET_TIME.toString())));
    }

    @Test
    @Transactional
    public void getScheduledMeal() throws Exception {
        // Initialize the database
        scheduledMealRepository.saveAndFlush(scheduledMeal);

        // Get the scheduledMeal
        restScheduledMealMockMvc.perform(get("/api/scheduled-meals/{id}", scheduledMeal.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(scheduledMeal.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.targetTime").value(DEFAULT_TARGET_TIME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingScheduledMeal() throws Exception {
        // Get the scheduledMeal
        restScheduledMealMockMvc.perform(get("/api/scheduled-meals/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateScheduledMeal() throws Exception {
        // Initialize the database
        scheduledMealRepository.saveAndFlush(scheduledMeal);
        int databaseSizeBeforeUpdate = scheduledMealRepository.findAll().size();

        // Update the scheduledMeal
        ScheduledMeal updatedScheduledMeal = scheduledMealRepository.findOne(scheduledMeal.getId());
        updatedScheduledMeal
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .targetTime(UPDATED_TARGET_TIME);

        restScheduledMealMockMvc.perform(put("/api/scheduled-meals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedScheduledMeal)))
            .andExpect(status().isOk());

        // Validate the ScheduledMeal in the database
        List<ScheduledMeal> scheduledMealList = scheduledMealRepository.findAll();
        assertThat(scheduledMealList).hasSize(databaseSizeBeforeUpdate);
        ScheduledMeal testScheduledMeal = scheduledMealList.get(scheduledMealList.size() - 1);
        assertThat(testScheduledMeal.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testScheduledMeal.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testScheduledMeal.getTargetTime()).isEqualTo(UPDATED_TARGET_TIME);
    }

    @Test
    @Transactional
    public void updateNonExistingScheduledMeal() throws Exception {
        int databaseSizeBeforeUpdate = scheduledMealRepository.findAll().size();

        // Create the ScheduledMeal

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restScheduledMealMockMvc.perform(put("/api/scheduled-meals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduledMeal)))
            .andExpect(status().isCreated());

        // Validate the ScheduledMeal in the database
        List<ScheduledMeal> scheduledMealList = scheduledMealRepository.findAll();
        assertThat(scheduledMealList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteScheduledMeal() throws Exception {
        // Initialize the database
        scheduledMealRepository.saveAndFlush(scheduledMeal);
        int databaseSizeBeforeDelete = scheduledMealRepository.findAll().size();

        // Get the scheduledMeal
        restScheduledMealMockMvc.perform(delete("/api/scheduled-meals/{id}", scheduledMeal.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ScheduledMeal> scheduledMealList = scheduledMealRepository.findAll();
        assertThat(scheduledMealList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScheduledMeal.class);
        ScheduledMeal scheduledMeal1 = new ScheduledMeal();
        scheduledMeal1.setId(1L);
        ScheduledMeal scheduledMeal2 = new ScheduledMeal();
        scheduledMeal2.setId(scheduledMeal1.getId());
        assertThat(scheduledMeal1).isEqualTo(scheduledMeal2);
        scheduledMeal2.setId(2L);
        assertThat(scheduledMeal1).isNotEqualTo(scheduledMeal2);
        scheduledMeal1.setId(null);
        assertThat(scheduledMeal1).isNotEqualTo(scheduledMeal2);
    }
}
