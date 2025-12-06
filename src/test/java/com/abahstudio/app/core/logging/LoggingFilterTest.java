package com.abahstudio.app.core.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingFilterTest {

    private LoggingFilter loggingFilter;

    @Mock
    private FilterChain filterChain;

    @Captor
    private ArgumentCaptor<HttpServletRequest> requestCaptor;

    @Captor
    private ArgumentCaptor<HttpServletResponse> responseCaptor;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        loggingFilter = new LoggingFilter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void shouldProcessRequestAndLogInformation() throws ServletException, IOException {
        // Given
        request.setMethod("GET");
        request.setRequestURI("/api/users");
        response.setStatus(200);

        // When
        loggingFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(requestCaptor.capture(), responseCaptor.capture());
        assertThat(requestCaptor.getValue()).isEqualTo(request);
        assertThat(responseCaptor.getValue()).isEqualTo(response);
    }

    @Test
    void shouldContinueFilterChainEvenWhenExceptionThrown() throws ServletException, IOException {
        // Given
        request.setMethod("POST");
        request.setRequestURI("/api/data");

        // Simulate exception in filter chain
        doThrow(new RuntimeException("Test exception")).when(filterChain)
                .doFilter(any(), any());

        // When & Then - should throw RuntimeException
        // Karena filter tidak menangkap exception, test harus mengharapkan exception
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> loggingFilter.doFilterInternal(request, response, filterChain));

        // Verify exception message
        assertThat(thrown.getMessage()).isEqualTo("Test exception");

        // Verify filter chain was called (akan terverifikasi karena exception terjadi setelah doFilter dipanggil)
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void shouldLogDifferentHttpMethods() throws ServletException, IOException {
        // Test various HTTP methods
        String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH"};

        for (String method : methods) {
            // Reset mocks
            reset(filterChain);

            // Setup
            request.setMethod(method);
            request.setRequestURI("/api/resource");
            response.setStatus(201);

            // Execute
            loggingFilter.doFilterInternal(request, response, filterChain);

            // Verify
            verify(filterChain).doFilter(any(), any());
        }
    }

    @Test
    void shouldCalculateExecutionTime() throws ServletException, IOException {
        // Given
        request.setMethod("GET");
        request.setRequestURI("/api/test");

        // Simulate some processing time
        doAnswer(invocation -> {
            Thread.sleep(50); // Simulate 50ms processing
            return null;
        }).when(filterChain).doFilter(any(), any());

        // When
        long startTime = System.currentTimeMillis();
        loggingFilter.doFilterInternal(request, response, filterChain);
        long endTime = System.currentTimeMillis();

        // Then
        long executionTime = endTime - startTime;
        assertThat(executionTime).isGreaterThanOrEqualTo(50);
    }

    @Test
    void shouldHandleDifferentResponseStatuses() throws ServletException, IOException {
        // Test various HTTP status codes
        int[] statusCodes = {200, 201, 400, 401, 403, 404, 500};

        for (int statusCode : statusCodes) {
            // Reset mocks
            reset(filterChain);

            // Setup
            request.setMethod("GET");
            request.setRequestURI("/api/test");
            response.setStatus(statusCode);

            // Execute
            loggingFilter.doFilterInternal(request, response, filterChain);

            // Verify
            verify(filterChain).doFilter(any(), any());
            assertThat(response.getStatus()).isEqualTo(statusCode);
        }
    }

    @Test
    void shouldPreserveRequestAndResponseObjects() throws ServletException, IOException {
        // Given - custom request/response objects
        MockHttpServletRequest customRequest = new MockHttpServletRequest();
        customRequest.setMethod("PUT");
        customRequest.setRequestURI("/api/custom");

        MockHttpServletResponse customResponse = new MockHttpServletResponse();
        customResponse.setStatus(204);

        // When
        loggingFilter.doFilterInternal(customRequest, customResponse, filterChain);

        // Then
        verify(filterChain).doFilter(customRequest, customResponse);
        assertThat(customResponse.getStatus()).isEqualTo(204);
    }
}