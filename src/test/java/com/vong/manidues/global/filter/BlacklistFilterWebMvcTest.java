package com.vong.manidues.global.filter;

import com.vong.manidues.WebMvcTestBase;
import com.vong.manidues.global.ViewController;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static com.vong.manidues.web.MvcUtility.buildMockGetRequest;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ViewController.class)
@Slf4j
public class BlacklistFilterWebMvcTest extends WebMvcTestBase {
    @Autowired
    public BlacklistFilterWebMvcTest(MockMvc mockMvc) {
        super(mockMvc);
    }

    @Test
    public void checkTrackRequestWith71Requests() throws Exception {
        var request = buildMockGetRequest("/").remoteAddress("127.0.0.2");

        for (int i = 0; i < 74; i++) {
            log.info("{}", i);
            if (i >= 70) {
                mockMvc.perform(request).andExpect(status().isNotFound());
                continue;
            }
            mockMvc.perform(request).andExpect(status().isOk());
        }

        var request2 = buildMockGetRequest("/").remoteAddress("127.0.0.3");

        for (int i = 0; i < 71; i++) {
            log.info("{}", i);
            if (i == 70) {
                mockMvc.perform(request2).andExpect(status().isNotFound());
                continue;
            }
            mockMvc.perform(request2).andExpect(status().isOk());
        }
    }
}
