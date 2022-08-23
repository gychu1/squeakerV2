package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Tweets;
import com.mycompany.myapp.repository.TweetsRepository;
import com.mycompany.myapp.service.dto.TweetsDTO;
import com.mycompany.myapp.service.mapper.TweetsMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TweetsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TweetsResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/tweets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TweetsRepository tweetsRepository;

    @Autowired
    private TweetsMapper tweetsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTweetsMockMvc;

    private Tweets tweets;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tweets createEntity(EntityManager em) {
        Tweets tweets = new Tweets().content(DEFAULT_CONTENT).createdAt(DEFAULT_CREATED_AT).updatedAt(DEFAULT_UPDATED_AT);
        return tweets;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tweets createUpdatedEntity(EntityManager em) {
        Tweets tweets = new Tweets().content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
        return tweets;
    }

    @BeforeEach
    public void initTest() {
        tweets = createEntity(em);
    }

    @Test
    @Transactional
    void createTweets() throws Exception {
        int databaseSizeBeforeCreate = tweetsRepository.findAll().size();
        // Create the Tweets
        TweetsDTO tweetsDTO = tweetsMapper.toDto(tweets);
        restTweetsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tweetsDTO)))
            .andExpect(status().isCreated());

        // Validate the Tweets in the database
        List<Tweets> tweetsList = tweetsRepository.findAll();
        assertThat(tweetsList).hasSize(databaseSizeBeforeCreate + 1);
        Tweets testTweets = tweetsList.get(tweetsList.size() - 1);
        assertThat(testTweets.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testTweets.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testTweets.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void createTweetsWithExistingId() throws Exception {
        // Create the Tweets with an existing ID
        tweets.setId(1L);
        TweetsDTO tweetsDTO = tweetsMapper.toDto(tweets);

        int databaseSizeBeforeCreate = tweetsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTweetsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tweetsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Tweets in the database
        List<Tweets> tweetsList = tweetsRepository.findAll();
        assertThat(tweetsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkContentIsRequired() throws Exception {
        int databaseSizeBeforeTest = tweetsRepository.findAll().size();
        // set the field null
        tweets.setContent(null);

        // Create the Tweets, which fails.
        TweetsDTO tweetsDTO = tweetsMapper.toDto(tweets);

        restTweetsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tweetsDTO)))
            .andExpect(status().isBadRequest());

        List<Tweets> tweetsList = tweetsRepository.findAll();
        assertThat(tweetsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTweets() throws Exception {
        // Initialize the database
        tweetsRepository.saveAndFlush(tweets);

        // Get all the tweetsList
        restTweetsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tweets.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getTweets() throws Exception {
        // Initialize the database
        tweetsRepository.saveAndFlush(tweets);

        // Get the tweets
        restTweetsMockMvc
            .perform(get(ENTITY_API_URL_ID, tweets.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tweets.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTweets() throws Exception {
        // Get the tweets
        restTweetsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTweets() throws Exception {
        // Initialize the database
        tweetsRepository.saveAndFlush(tweets);

        int databaseSizeBeforeUpdate = tweetsRepository.findAll().size();

        // Update the tweets
        Tweets updatedTweets = tweetsRepository.findById(tweets.getId()).get();
        // Disconnect from session so that the updates on updatedTweets are not directly saved in db
        em.detach(updatedTweets);
        updatedTweets.content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
        TweetsDTO tweetsDTO = tweetsMapper.toDto(updatedTweets);

        restTweetsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tweetsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tweetsDTO))
            )
            .andExpect(status().isOk());

        // Validate the Tweets in the database
        List<Tweets> tweetsList = tweetsRepository.findAll();
        assertThat(tweetsList).hasSize(databaseSizeBeforeUpdate);
        Tweets testTweets = tweetsList.get(tweetsList.size() - 1);
        assertThat(testTweets.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testTweets.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testTweets.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingTweets() throws Exception {
        int databaseSizeBeforeUpdate = tweetsRepository.findAll().size();
        tweets.setId(count.incrementAndGet());

        // Create the Tweets
        TweetsDTO tweetsDTO = tweetsMapper.toDto(tweets);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTweetsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tweetsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tweetsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tweets in the database
        List<Tweets> tweetsList = tweetsRepository.findAll();
        assertThat(tweetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTweets() throws Exception {
        int databaseSizeBeforeUpdate = tweetsRepository.findAll().size();
        tweets.setId(count.incrementAndGet());

        // Create the Tweets
        TweetsDTO tweetsDTO = tweetsMapper.toDto(tweets);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTweetsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tweetsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tweets in the database
        List<Tweets> tweetsList = tweetsRepository.findAll();
        assertThat(tweetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTweets() throws Exception {
        int databaseSizeBeforeUpdate = tweetsRepository.findAll().size();
        tweets.setId(count.incrementAndGet());

        // Create the Tweets
        TweetsDTO tweetsDTO = tweetsMapper.toDto(tweets);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTweetsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tweetsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tweets in the database
        List<Tweets> tweetsList = tweetsRepository.findAll();
        assertThat(tweetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTweetsWithPatch() throws Exception {
        // Initialize the database
        tweetsRepository.saveAndFlush(tweets);

        int databaseSizeBeforeUpdate = tweetsRepository.findAll().size();

        // Update the tweets using partial update
        Tweets partialUpdatedTweets = new Tweets();
        partialUpdatedTweets.setId(tweets.getId());

        partialUpdatedTweets.content(UPDATED_CONTENT);

        restTweetsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTweets.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTweets))
            )
            .andExpect(status().isOk());

        // Validate the Tweets in the database
        List<Tweets> tweetsList = tweetsRepository.findAll();
        assertThat(tweetsList).hasSize(databaseSizeBeforeUpdate);
        Tweets testTweets = tweetsList.get(tweetsList.size() - 1);
        assertThat(testTweets.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testTweets.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testTweets.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateTweetsWithPatch() throws Exception {
        // Initialize the database
        tweetsRepository.saveAndFlush(tweets);

        int databaseSizeBeforeUpdate = tweetsRepository.findAll().size();

        // Update the tweets using partial update
        Tweets partialUpdatedTweets = new Tweets();
        partialUpdatedTweets.setId(tweets.getId());

        partialUpdatedTweets.content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        restTweetsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTweets.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTweets))
            )
            .andExpect(status().isOk());

        // Validate the Tweets in the database
        List<Tweets> tweetsList = tweetsRepository.findAll();
        assertThat(tweetsList).hasSize(databaseSizeBeforeUpdate);
        Tweets testTweets = tweetsList.get(tweetsList.size() - 1);
        assertThat(testTweets.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testTweets.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testTweets.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingTweets() throws Exception {
        int databaseSizeBeforeUpdate = tweetsRepository.findAll().size();
        tweets.setId(count.incrementAndGet());

        // Create the Tweets
        TweetsDTO tweetsDTO = tweetsMapper.toDto(tweets);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTweetsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tweetsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tweetsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tweets in the database
        List<Tweets> tweetsList = tweetsRepository.findAll();
        assertThat(tweetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTweets() throws Exception {
        int databaseSizeBeforeUpdate = tweetsRepository.findAll().size();
        tweets.setId(count.incrementAndGet());

        // Create the Tweets
        TweetsDTO tweetsDTO = tweetsMapper.toDto(tweets);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTweetsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tweetsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tweets in the database
        List<Tweets> tweetsList = tweetsRepository.findAll();
        assertThat(tweetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTweets() throws Exception {
        int databaseSizeBeforeUpdate = tweetsRepository.findAll().size();
        tweets.setId(count.incrementAndGet());

        // Create the Tweets
        TweetsDTO tweetsDTO = tweetsMapper.toDto(tweets);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTweetsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(tweetsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tweets in the database
        List<Tweets> tweetsList = tweetsRepository.findAll();
        assertThat(tweetsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTweets() throws Exception {
        // Initialize the database
        tweetsRepository.saveAndFlush(tweets);

        int databaseSizeBeforeDelete = tweetsRepository.findAll().size();

        // Delete the tweets
        restTweetsMockMvc
            .perform(delete(ENTITY_API_URL_ID, tweets.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Tweets> tweetsList = tweetsRepository.findAll();
        assertThat(tweetsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
