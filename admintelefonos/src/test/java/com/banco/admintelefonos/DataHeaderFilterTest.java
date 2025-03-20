package com.banco.admintelefonos;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;

import com.banco.admintelefonos.config.DataHeaderFilter;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class DataHeaderFilterTest {

    private DataHeaderFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private FilterChain filterChain;

    private HttpServletResponse response;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new DataHeaderFilter("MIEXAMENPRUEBA");
        response = new MockHttpServletResponse();
    }

    @Test
    public void testDoFilterInternal_ValidHeader() throws ServletException, IOException {
        when(request.getHeader("DATA"))
                .thenReturn("bXIHHGDUTBaGk7VX3Uj+7dfHY5N6nwK684IKIYvucD8=");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertEquals(200, response.getStatus()); // Expect 200 OK after passing the filter
    }

    @Test
    public void testDoFilterInternal_InvalidHeader() throws ServletException, IOException {
        when(request.getHeader("DATA")).thenReturn("invalidHash");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        assertEquals(401, response.getStatus());
        assertEquals("Unauthorized: Invalid DATA header", ((MockHttpServletResponse) response).getContentAsString());
    }

    @Test
    public void testDoFilterInternal_MissingHeader() throws ServletException, IOException {
        when(request.getHeader("DATA")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        assertEquals(401, response.getStatus());
        assertEquals("Unauthorized: Invalid DATA header", ((MockHttpServletResponse) response).getContentAsString());
    }
}