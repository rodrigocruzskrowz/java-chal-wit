package wit.calc.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import wit.calc.calculator.service.CalcListener;
import wit.calc.common.dto.CalcRequest;
import wit.calc.common.dto.CalcResponse;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CalcListenerTest {

    private CalcListener calcListener;

    @BeforeEach
    void setUp() {
        calcListener = new CalcListener();
    }

    @Test
    void shouldReturnCorrectSum() {
        CalcRequest request = new CalcRequest("uid-1", '+', new BigDecimal("1"), new BigDecimal("2"));
        CalcResponse response = calcListener.performCalc(request);
        assertEquals(new BigDecimal("3"), response.getResult());
        assertEquals("uid-1", response.getUid());
    }

    @Test
    void shouldReturnCorrectSubtraction() {
        CalcRequest request = new CalcRequest("uid-2", '-', new BigDecimal("2"), new BigDecimal("1"));
        CalcResponse response = calcListener.performCalc(request);
        assertEquals(new BigDecimal("1"), response.getResult());
    }

    @Test
    void shouldReturnCorrectMultiplication() {
        CalcRequest request = new CalcRequest("uid-3", '*', new BigDecimal("1"), new BigDecimal("2"));
        CalcResponse response = calcListener.performCalc(request);
        assertEquals(new BigDecimal("2"), response.getResult());
    }

    @Test
    void shouldReturnCorrectDivision() {
        CalcRequest request = new CalcRequest("uid-4", '/', new BigDecimal("1"), new BigDecimal("2"));
        CalcResponse response = calcListener.performCalc(request);
        assertEquals(0, new BigDecimal("0.5").compareTo(response.getResult()));
    }

    @Test
    void shouldThrowExceptionForInvalidOperation() {
        CalcRequest request = new CalcRequest("uid-5", '%', new BigDecimal("1"), new BigDecimal("2"));
        assertThrows(IllegalArgumentException.class, () -> calcListener.performCalc(request));
    }

    @Test
    void shouldHandleDecimalValues() {
        CalcRequest request = new CalcRequest("uid-6", '+', new BigDecimal("1.5"), new BigDecimal("2.3"));
        CalcResponse response = calcListener.performCalc(request);
        assertEquals(0, new BigDecimal("3.8").compareTo(response.getResult()));
    }

    @Test
    void shouldHandleNegativeNumbers() {
        CalcRequest request = new CalcRequest("uid-7", '-', new BigDecimal("1"), new BigDecimal("2"));
        CalcResponse response = calcListener.performCalc(request);
        assertEquals(0, new BigDecimal("-1").compareTo(response.getResult()));
    }
}
