package com.abahstudio.app.core.config;

import com.abahstudio.app.domain.auth.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({ActuatorSecurityConfig.class, ActuatorSecurityConfigTest.TestWebConfig.class})
class ActuatorSecurityConfigTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CustomUserDetailsService userDetailsService; // hindari NoSuchBeanDefinition

    /* ---------- health ---------- */
    @Test
    @WithAnonymousUser
    @DisplayName("GET /actuator/health -> 200 untuk anonymous")
    void healthIsOpen() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    /* ---------- info ---------- */
    @Test
    @WithAnonymousUser
    @DisplayName("GET /actuator/info -> 200 untuk anonymous")
    void infoIsOpen() throws Exception {
        mvc.perform(get("/actuator/info"))
                .andExpect(status().isOk());
    }

    /* ---------- env tanpa role ---------- */
    @Test
    @DisplayName("GET /actuator/env -> 403 kalau tidak login")
    void envRequiresLogin() throws Exception {
        mvc.perform(get("/actuator/env"))
                .andExpect(status().isForbidden());
    }

    /* ---------- env dengan role salah ---------- */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /actuator/env -> 403 kalau login tapi bukan ACTUATOR_ADMIN")
    void envRequiresCorrectRole() throws Exception {
        mvc.perform(get("/actuator/env"))
                .andExpect(status().isForbidden());
    }

    /* ---------- env dengan role benar ---------- */
    @Test
    @WithMockUser(roles = "ACTUATOR_ADMIN")
    @DisplayName("GET /actuator/env -> 200 kalau ACTUATOR_ADMIN")
    void envOkWithRightRole() throws Exception {
        mvc.perform(get("/actuator/env"))
                .andExpect(status().isOk());
    }

    /* ---------- endpoint di luar actuator ---------- */
    @Test
    @WithAnonymousUser
    @DisplayName("GET /api/hello -> tetap 403 (tidak di-cover chain ini)")
    void otherEndpointsStillSecured() throws Exception {
        mvc.perform(get("/api/hello"))
                .andExpect(status().isForbidden());
    }

    /* Config mini agar HandlerMappingIntrospector muncul */
    @EnableWebMvc
    static class TestWebConfig {
    }
}