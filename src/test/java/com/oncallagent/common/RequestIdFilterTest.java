package com.oncallagent.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

/** RequestIdFilter:每个响应都带 X-Request-Id,且透传上游传入的值。 */
@WebMvcTest(HealthController.class)
class RequestIdFilterTest {

    @Autowired private MockMvc mockMvc;

    @Test
    void generates_request_id_when_absent() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Request-Id"));
    }

    @Test
    void echoes_incoming_request_id() throws Exception {
        mockMvc.perform(get("/api/health").header("X-Request-Id", "abc-123"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Request-Id", "abc-123"));
    }
}
