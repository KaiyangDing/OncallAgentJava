package com.oncallagent.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** 用临时会抛异常的 Controller + 全局异常处理器,验证异常被转成统一信封。 */
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc =
                MockMvcBuilders.standaloneSetup(new ThrowingController())
                        .setControllerAdvice(new GlobalExceptionHandler())
                        .build();
    }

    @Test
    void unexpected_exception_maps_to_500_envelope() throws Exception {
        mockMvc.perform(get("/test/boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INTERNAL_ERROR"));
    }

    @Test
    void response_status_exception_maps_to_its_status() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value("资源不存在"));
    }

    @RestController
    static class ThrowingController {
        @GetMapping("/test/boom")
        String boom() {
            throw new RuntimeException("boom");
        }

        @GetMapping("/test/not-found")
        String notFound() {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "资源不存在");
        }
    }
}
