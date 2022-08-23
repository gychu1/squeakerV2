package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TweetsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tweets.class);
        Tweets tweets1 = new Tweets();
        tweets1.setId(1L);
        Tweets tweets2 = new Tweets();
        tweets2.setId(tweets1.getId());
        assertThat(tweets1).isEqualTo(tweets2);
        tweets2.setId(2L);
        assertThat(tweets1).isNotEqualTo(tweets2);
        tweets1.setId(null);
        assertThat(tweets1).isNotEqualTo(tweets2);
    }
}
