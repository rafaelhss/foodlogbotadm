package com.foodlog.web.rest;

import com.foodlog.FoodlogbotadmApp;

import com.foodlog.domain.UserTelegram;
import com.foodlog.repository.UserTelegramRepository;
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
 * Test class for the UserTelegramResource REST controller.
 *
 * @see UserTelegramResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FoodlogbotadmApp.class)
public class UserTelegramResourceIntTest {

    private static final Integer DEFAULT_TELEGRAM_ID = 1;
    private static final Integer UPDATED_TELEGRAM_ID = 2;

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    @Autowired
    private UserTelegramRepository userTelegramRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restUserTelegramMockMvc;

    private UserTelegram userTelegram;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        UserTelegramResource userTelegramResource = new UserTelegramResource(userTelegramRepository);
        this.restUserTelegramMockMvc = MockMvcBuilders.standaloneSetup(userTelegramResource)
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
    public static UserTelegram createEntity(EntityManager em) {
        UserTelegram userTelegram = new UserTelegram()
            .telegramId(DEFAULT_TELEGRAM_ID)
            .first_name(DEFAULT_FIRST_NAME)
            .last_name(DEFAULT_LAST_NAME);
        return userTelegram;
    }

    @Before
    public void initTest() {
        userTelegram = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserTelegram() throws Exception {
        int databaseSizeBeforeCreate = userTelegramRepository.findAll().size();

        // Create the UserTelegram
        restUserTelegramMockMvc.perform(post("/api/user-telegrams")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userTelegram)))
            .andExpect(status().isCreated());

        // Validate the UserTelegram in the database
        List<UserTelegram> userTelegramList = userTelegramRepository.findAll();
        assertThat(userTelegramList).hasSize(databaseSizeBeforeCreate + 1);
        UserTelegram testUserTelegram = userTelegramList.get(userTelegramList.size() - 1);
        assertThat(testUserTelegram.getTelegramId()).isEqualTo(DEFAULT_TELEGRAM_ID);
        assertThat(testUserTelegram.getFirst_name()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testUserTelegram.getLast_name()).isEqualTo(DEFAULT_LAST_NAME);
    }

    @Test
    @Transactional
    public void createUserTelegramWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userTelegramRepository.findAll().size();

        // Create the UserTelegram with an existing ID
        userTelegram.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserTelegramMockMvc.perform(post("/api/user-telegrams")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userTelegram)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<UserTelegram> userTelegramList = userTelegramRepository.findAll();
        assertThat(userTelegramList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllUserTelegrams() throws Exception {
        // Initialize the database
        userTelegramRepository.saveAndFlush(userTelegram);

        // Get all the userTelegramList
        restUserTelegramMockMvc.perform(get("/api/user-telegrams?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userTelegram.getId().intValue())))
            .andExpect(jsonPath("$.[*].telegramId").value(hasItem(DEFAULT_TELEGRAM_ID)))
            .andExpect(jsonPath("$.[*].first_name").value(hasItem(DEFAULT_FIRST_NAME.toString())))
            .andExpect(jsonPath("$.[*].last_name").value(hasItem(DEFAULT_LAST_NAME.toString())));
    }

    @Test
    @Transactional
    public void getUserTelegram() throws Exception {
        // Initialize the database
        userTelegramRepository.saveAndFlush(userTelegram);

        // Get the userTelegram
        restUserTelegramMockMvc.perform(get("/api/user-telegrams/{id}", userTelegram.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(userTelegram.getId().intValue()))
            .andExpect(jsonPath("$.telegramId").value(DEFAULT_TELEGRAM_ID))
            .andExpect(jsonPath("$.first_name").value(DEFAULT_FIRST_NAME.toString()))
            .andExpect(jsonPath("$.last_name").value(DEFAULT_LAST_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingUserTelegram() throws Exception {
        // Get the userTelegram
        restUserTelegramMockMvc.perform(get("/api/user-telegrams/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserTelegram() throws Exception {
        // Initialize the database
        userTelegramRepository.saveAndFlush(userTelegram);
        int databaseSizeBeforeUpdate = userTelegramRepository.findAll().size();

        // Update the userTelegram
        UserTelegram updatedUserTelegram = userTelegramRepository.findOne(userTelegram.getId());
        updatedUserTelegram
            .telegramId(UPDATED_TELEGRAM_ID)
            .first_name(UPDATED_FIRST_NAME)
            .last_name(UPDATED_LAST_NAME);

        restUserTelegramMockMvc.perform(put("/api/user-telegrams")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedUserTelegram)))
            .andExpect(status().isOk());

        // Validate the UserTelegram in the database
        List<UserTelegram> userTelegramList = userTelegramRepository.findAll();
        assertThat(userTelegramList).hasSize(databaseSizeBeforeUpdate);
        UserTelegram testUserTelegram = userTelegramList.get(userTelegramList.size() - 1);
        assertThat(testUserTelegram.getTelegramId()).isEqualTo(UPDATED_TELEGRAM_ID);
        assertThat(testUserTelegram.getFirst_name()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testUserTelegram.getLast_name()).isEqualTo(UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingUserTelegram() throws Exception {
        int databaseSizeBeforeUpdate = userTelegramRepository.findAll().size();

        // Create the UserTelegram

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restUserTelegramMockMvc.perform(put("/api/user-telegrams")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userTelegram)))
            .andExpect(status().isCreated());

        // Validate the UserTelegram in the database
        List<UserTelegram> userTelegramList = userTelegramRepository.findAll();
        assertThat(userTelegramList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteUserTelegram() throws Exception {
        // Initialize the database
        userTelegramRepository.saveAndFlush(userTelegram);
        int databaseSizeBeforeDelete = userTelegramRepository.findAll().size();

        // Get the userTelegram
        restUserTelegramMockMvc.perform(delete("/api/user-telegrams/{id}", userTelegram.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<UserTelegram> userTelegramList = userTelegramRepository.findAll();
        assertThat(userTelegramList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserTelegram.class);
        UserTelegram userTelegram1 = new UserTelegram();
        userTelegram1.setId(1L);
        UserTelegram userTelegram2 = new UserTelegram();
        userTelegram2.setId(userTelegram1.getId());
        assertThat(userTelegram1).isEqualTo(userTelegram2);
        userTelegram2.setId(2L);
        assertThat(userTelegram1).isNotEqualTo(userTelegram2);
        userTelegram1.setId(null);
        assertThat(userTelegram1).isNotEqualTo(userTelegram2);
    }
}
