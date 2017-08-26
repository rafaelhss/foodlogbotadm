package com.foodlog.web.rest;

import com.foodlog.FoodlogbotadmApp;

import com.foodlog.domain.MealLog;
import com.foodlog.repository.MealLogRepository;
import com.foodlog.service.MealLogService;
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
import org.springframework.util.Base64Utils;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the MealLogResource REST controller.
 *
 * @see MealLogResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodlogbotadmApp.class)
public class MealLogResourceIntTest {

    private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

    private static final Instant DEFAULT_MEAL_DATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_MEAL_DATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final Long DEFAULT_UPDATE_ID = 1L;
    private static final Long UPDATED_UPDATE_ID = 2L;

    @Autowired
    private MealLogRepository mealLogRepository;

    @Autowired
    private MealLogService mealLogService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMealLogMockMvc;

    private MealLog mealLog;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MealLogResource mealLogResource = new MealLogResource(mealLogService);
        this.restMealLogMockMvc = MockMvcBuilders.standaloneSetup(mealLogResource)
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
    public static MealLog createEntity(EntityManager em) {
        MealLog mealLog = new MealLog()
            .photo(DEFAULT_PHOTO)
            .photoContentType(DEFAULT_PHOTO_CONTENT_TYPE)
            .mealDateTime(DEFAULT_MEAL_DATE_TIME)
            .comment(DEFAULT_COMMENT)
            .updateId(DEFAULT_UPDATE_ID);
        return mealLog;
    }

    @Before
    public void initTest() {
        mealLog = createEntity(em);
    }

    @Test
    @Transactional
    public void createMealLog() throws Exception {
        int databaseSizeBeforeCreate = mealLogRepository.findAll().size();

        // Create the MealLog
        restMealLogMockMvc.perform(post("/api/meal-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mealLog)))
            .andExpect(status().isCreated());

        // Validate the MealLog in the database
        List<MealLog> mealLogList = mealLogRepository.findAll();
        assertThat(mealLogList).hasSize(databaseSizeBeforeCreate + 1);
        MealLog testMealLog = mealLogList.get(mealLogList.size() - 1);
        assertThat(testMealLog.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testMealLog.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
        assertThat(testMealLog.getMealDateTime()).isEqualTo(DEFAULT_MEAL_DATE_TIME);
        assertThat(testMealLog.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testMealLog.getUpdateId()).isEqualTo(DEFAULT_UPDATE_ID);
    }

    @Test
    @Transactional
    public void createMealLogWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = mealLogRepository.findAll().size();

        // Create the MealLog with an existing ID
        mealLog.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMealLogMockMvc.perform(post("/api/meal-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mealLog)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<MealLog> mealLogList = mealLogRepository.findAll();
        assertThat(mealLogList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkPhotoIsRequired() throws Exception {
        int databaseSizeBeforeTest = mealLogRepository.findAll().size();
        // set the field null
        mealLog.setPhoto(null);

        // Create the MealLog, which fails.

        restMealLogMockMvc.perform(post("/api/meal-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mealLog)))
            .andExpect(status().isBadRequest());

        List<MealLog> mealLogList = mealLogRepository.findAll();
        assertThat(mealLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMealDateTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = mealLogRepository.findAll().size();
        // set the field null
        mealLog.setMealDateTime(null);

        // Create the MealLog, which fails.

        restMealLogMockMvc.perform(post("/api/meal-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mealLog)))
            .andExpect(status().isBadRequest());

        List<MealLog> mealLogList = mealLogRepository.findAll();
        assertThat(mealLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllMealLogs() throws Exception {
        // Initialize the database
        mealLogRepository.saveAndFlush(mealLog);

        // Get all the mealLogList
        restMealLogMockMvc.perform(get("/api/meal-logs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mealLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].mealDateTime").value(hasItem(DEFAULT_MEAL_DATE_TIME.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].updateId").value(hasItem(DEFAULT_UPDATE_ID.intValue())));
    }

    @Test
    @Transactional
    public void getMealLog() throws Exception {
        // Initialize the database
        mealLogRepository.saveAndFlush(mealLog);

        // Get the mealLog
        restMealLogMockMvc.perform(get("/api/meal-logs/{id}", mealLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(mealLog.getId().intValue()))
            .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo").value(Base64Utils.encodeToString(DEFAULT_PHOTO)))
            .andExpect(jsonPath("$.mealDateTime").value(DEFAULT_MEAL_DATE_TIME.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.updateId").value(DEFAULT_UPDATE_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingMealLog() throws Exception {
        // Get the mealLog
        restMealLogMockMvc.perform(get("/api/meal-logs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMealLog() throws Exception {
        // Initialize the database
        mealLogService.save(mealLog);

        int databaseSizeBeforeUpdate = mealLogRepository.findAll().size();

        // Update the mealLog
        MealLog updatedMealLog = mealLogRepository.findOne(mealLog.getId());
        updatedMealLog
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .mealDateTime(UPDATED_MEAL_DATE_TIME)
            .comment(UPDATED_COMMENT)
            .updateId(UPDATED_UPDATE_ID);

        restMealLogMockMvc.perform(put("/api/meal-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedMealLog)))
            .andExpect(status().isOk());

        // Validate the MealLog in the database
        List<MealLog> mealLogList = mealLogRepository.findAll();
        assertThat(mealLogList).hasSize(databaseSizeBeforeUpdate);
        MealLog testMealLog = mealLogList.get(mealLogList.size() - 1);
        assertThat(testMealLog.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testMealLog.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testMealLog.getMealDateTime()).isEqualTo(UPDATED_MEAL_DATE_TIME);
        assertThat(testMealLog.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testMealLog.getUpdateId()).isEqualTo(UPDATED_UPDATE_ID);
    }

    @Test
    @Transactional
    public void updateNonExistingMealLog() throws Exception {
        int databaseSizeBeforeUpdate = mealLogRepository.findAll().size();

        // Create the MealLog

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restMealLogMockMvc.perform(put("/api/meal-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(mealLog)))
            .andExpect(status().isCreated());

        // Validate the MealLog in the database
        List<MealLog> mealLogList = mealLogRepository.findAll();
        assertThat(mealLogList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteMealLog() throws Exception {
        // Initialize the database
        mealLogService.save(mealLog);

        int databaseSizeBeforeDelete = mealLogRepository.findAll().size();

        // Get the mealLog
        restMealLogMockMvc.perform(delete("/api/meal-logs/{id}", mealLog.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<MealLog> mealLogList = mealLogRepository.findAll();
        assertThat(mealLogList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MealLog.class);
        MealLog mealLog1 = new MealLog();
        mealLog1.setId(1L);
        MealLog mealLog2 = new MealLog();
        mealLog2.setId(mealLog1.getId());
        assertThat(mealLog1).isEqualTo(mealLog2);
        mealLog2.setId(2L);
        assertThat(mealLog1).isNotEqualTo(mealLog2);
        mealLog1.setId(null);
        assertThat(mealLog1).isNotEqualTo(mealLog2);
    }
}
