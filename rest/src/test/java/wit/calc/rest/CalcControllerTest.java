package wit.calc.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import wit.calc.common.dto.CalcResponse;
import wit.calc.rest.controller.CalcController;
import wit.calc.rest.service.CalcService;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalcController.class)
public class CalcControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CalcService calcService;

    @Test
    void shouldReturnSumResult() throws Exception {
        when(calcService.sendRequest(new BigDecimal("1"), new BigDecimal("2"), '+')).thenReturn(new CalcResponse("uid-1", new BigDecimal("3")));

        mockMvc.perform(get("/api/v1/calc/sum")
                        .param("a", "1")
                        .param("b", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(3));
    }

    @Test
    void shouldReturnSubtractionResult() throws Exception {
        when(calcService.sendRequest(new BigDecimal("2"), new BigDecimal("1"), '-')).thenReturn(new CalcResponse("uid-1", new BigDecimal("1")));

        mockMvc.perform(get("/api/v1/calc/subtraction")
                        .param("a", "2")
                        .param("b", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(1));
    }

    @Test
    void shouldReturnMultiplicationResult() throws Exception {
        when(calcService.sendRequest(new BigDecimal("1"), new BigDecimal("2"), '*')).thenReturn(new CalcResponse("uid-1", new BigDecimal("2")));

        mockMvc.perform(get("/api/v1/calc/multiplication")
                        .param("a", "1")
                        .param("b", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(2));
    }

    @Test
    void shouldReturnDivisionResult() throws Exception {
        when(calcService.sendRequest(new BigDecimal("1"), new BigDecimal("2"), '/')).thenReturn(new CalcResponse("uid-1", new BigDecimal("0.5")));

        mockMvc.perform(get("/api/v1/calc/division")
                        .param("a", "1")
                        .param("b", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(0.5));
    }

    @Test
    void shouldReturnBadRequestOnDivisionByZero() throws Exception {
        when(calcService.sendRequest(any(), eq(BigDecimal.ZERO), eq('/'))).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Division by zero is not allowed."));

        mockMvc.perform(get("/api/v1/calc/division")
                        .param("a", "10")
                        .param("b", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenParamIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/calc/sum")
                        .param("a", "1"))
                .andExpect(status().isBadRequest());
    }
}
