package com.foodlog.web.rest;

import com.foodlog.FoodlogbotadmApp;

import com.foodlog.domain.BodyLog;
import com.foodlog.domain.User;
import com.foodlog.repository.BodyLogRepository;
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
 * Test class for the BodyLogResource REST controller.
 *
 * @see BodyLogResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodlogbotadmApp.class)
public class BodyLogResourceIntTest {

    private static final byte[] DEFAULT_PHOTO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PHOTO = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_PHOTO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PHOTO_CONTENT_TYPE = "image/png";

    private static final Instant DEFAULT_BODY_LOG_DATETIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_BODY_LOG_DATETIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_UPDATE_ID = 1L;
    private static final Long UPDATED_UPDATE_ID = 2L;

    @Autowired
    private BodyLogRepository bodyLogRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restBodyLogMockMvc;

    private BodyLog bodyLog;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BodyLogResource bodyLogResource = new BodyLogResource(bodyLogRepository);
        this.restBodyLogMockMvc = MockMvcBuilders.standaloneSetup(bodyLogResource)
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
    public static BodyLog createEntity(EntityManager em) {
        BodyLog bodyLog = new BodyLog()
            .photo(DEFAULT_PHOTO)
            .photoContentType(DEFAULT_PHOTO_CONTENT_TYPE)
            .bodyLogDatetime(DEFAULT_BODY_LOG_DATETIME)
            .updateId(DEFAULT_UPDATE_ID);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        bodyLog.setUser(user);
        return bodyLog;
    }

    @Before
    public void initTest() {
        bodyLog = createEntity(em);
    }

    @Test
    @Transactional
    public void createBodyLog() throws Exception {
        int databaseSizeBeforeCreate = bodyLogRepository.findAll().size();

        // Create the BodyLog
        restBodyLogMockMvc.perform(post("/api/body-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bodyLog)))
            .andExpect(status().isCreated());

        // Validate the BodyLog in the database
        List<BodyLog> bodyLogList = bodyLogRepository.findAll();
        assertThat(bodyLogList).hasSize(databaseSizeBeforeCreate + 1);
        BodyLog testBodyLog = bodyLogList.get(bodyLogList.size() - 1);
        assertThat(testBodyLog.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testBodyLog.getPhotoContentType()).isEqualTo(DEFAULT_PHOTO_CONTENT_TYPE);
        assertThat(testBodyLog.getBodyLogDatetime()).isEqualTo(DEFAULT_BODY_LOG_DATETIME);
        assertThat(testBodyLog.getUpdateId()).isEqualTo(DEFAULT_UPDATE_ID);
    }

    @Test
    @Transactional
    public void createBodyLogWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = bodyLogRepository.findAll().size();

        // Create the BodyLog with an existing ID
        bodyLog.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBodyLogMockMvc.perform(post("/api/body-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bodyLog)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<BodyLog> bodyLogList = bodyLogRepository.findAll();
        assertThat(bodyLogList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkPhotoIsRequired() throws Exception {
        int databaseSizeBeforeTest = bodyLogRepository.findAll().size();
        // set the field null
        bodyLog.setPhoto(null);

        // Create the BodyLog, which fails.

        restBodyLogMockMvc.perform(post("/api/body-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bodyLog)))
            .andExpect(status().isBadRequest());

        List<BodyLog> bodyLogList = bodyLogRepository.findAll();
        assertThat(bodyLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkBodyLogDatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = bodyLogRepository.findAll().size();
        // set the field null
        bodyLog.setBodyLogDatetime(null);

        // Create the BodyLog, which fails.

        restBodyLogMockMvc.perform(post("/api/body-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bodyLog)))
            .andExpect(status().isBadRequest());

        List<BodyLog> bodyLogList = bodyLogRepository.findAll();
        assertThat(bodyLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUpdateIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = bodyLogRepository.findAll().size();
        // set the field null
        bodyLog.setUpdateId(null);

        // Create the BodyLog, which fails.

        restBodyLogMockMvc.perform(post("/api/body-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bodyLog)))
            .andExpect(status().isBadRequest());

        List<BodyLog> bodyLogList = bodyLogRepository.findAll();
        assertThat(bodyLogList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBodyLogs() throws Exception {
        // Initialize the database
        bodyLogRepository.saveAndFlush(bodyLog);

        // Get all the bodyLogList
        restBodyLogMockMvc.perform(get("/api/body-logs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bodyLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].photoContentType").value(hasItem(DEFAULT_PHOTO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(Base64Utils.encodeToString(DEFAULT_PHOTO))))
            .andExpect(jsonPath("$.[*].bodyLogDatetime").value(hasItem(DEFAULT_BODY_LOG_DATETIME.toString())))
            .andExpect(jsonPath("$.[*].updateId").value(hasItem(DEFAULT_UPDATE_ID.intValue())));
    }

    @Test
    @Transactional
    public void getBodyLog() throws Exception {
        // Initialize the database
        bodyLogRepository.saveAndFlush(bodyLog);

        // Get the bodyLog
        restBodyLogMockMvc.perform(get("/api/body-logs/{id}", bodyLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(bodyLog.getId().intValue()))
            .andExpect(jsonPath("$.photoContentType").value(DEFAULT_PHOTO_CONTENT_TYPE))
            .andExpect(jsonPath("$.photo").value(Base64Utils.encodeToString(DEFAULT_PHOTO)))
            .andExpect(jsonPath("$.bodyLogDatetime").value(DEFAULT_BODY_LOG_DATETIME.toString()))
            .andExpect(jsonPath("$.updateId").value(DEFAULT_UPDATE_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingBodyLog() throws Exception {
        // Get the bodyLog
        restBodyLogMockMvc.perform(get("/api/body-logs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBodyLog() throws Exception {
        // Initialize the database
        bodyLogRepository.saveAndFlush(bodyLog);
        int databaseSizeBeforeUpdate = bodyLogRepository.findAll().size();

        // Update the bodyLog
        BodyLog updatedBodyLog = bodyLogRepository.findOne(bodyLog.getId());
        updatedBodyLog
            .photo(UPDATED_PHOTO)
            .photoContentType(UPDATED_PHOTO_CONTENT_TYPE)
            .bodyLogDatetime(UPDATED_BODY_LOG_DATETIME)
            .updateId(UPDATED_UPDATE_ID);

        restBodyLogMockMvc.perform(put("/api/body-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBodyLog)))
            .andExpect(status().isOk());

        // Validate the BodyLog in the database
        List<BodyLog> bodyLogList = bodyLogRepository.findAll();
        assertThat(bodyLogList).hasSize(databaseSizeBeforeUpdate);
        BodyLog testBodyLog = bodyLogList.get(bodyLogList.size() - 1);
        assertThat(testBodyLog.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testBodyLog.getPhotoContentType()).isEqualTo(UPDATED_PHOTO_CONTENT_TYPE);
        assertThat(testBodyLog.getBodyLogDatetime()).isEqualTo(UPDATED_BODY_LOG_DATETIME);
        assertThat(testBodyLog.getUpdateId()).isEqualTo(UPDATED_UPDATE_ID);
    }

    @Test
    @Transactional
    public void updateNonExistingBodyLog() throws Exception {
        int databaseSizeBeforeUpdate = bodyLogRepository.findAll().size();

        // Create the BodyLog

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBodyLogMockMvc.perform(put("/api/body-logs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bodyLog)))
            .andExpect(status().isCreated());

        // Validate the BodyLog in the database
        List<BodyLog> bodyLogList = bodyLogRepository.findAll();
        assertThat(bodyLogList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteBodyLog() throws Exception {
        // Initialize the database
        bodyLogRepository.saveAndFlush(bodyLog);
        int databaseSizeBeforeDelete = bodyLogRepository.findAll().size();

        // Get the bodyLog
        restBodyLogMockMvc.perform(delete("/api/body-logs/{id}", bodyLog.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<BodyLog> bodyLogList = bodyLogRepository.findAll();
        assertThat(bodyLogList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BodyLog.class);
        BodyLog bodyLog1 = new BodyLog();
        bodyLog1.setId(1L);
        BodyLog bodyLog2 = new BodyLog();
        bodyLog2.setId(bodyLog1.getId());
        assertThat(bodyLog1).isEqualTo(bodyLog2);
        bodyLog2.setId(2L);
        assertThat(bodyLog1).isNotEqualTo(bodyLog2);
        bodyLog1.setId(null);
        assertThat(bodyLog1).isNotEqualTo(bodyLog2);
    }
}
