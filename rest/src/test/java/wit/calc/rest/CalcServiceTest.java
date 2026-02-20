package wit.calc.rest;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import wit.calc.common.dto.CalcRequest;
import wit.calc.common.dto.CalcResponse;
import wit.calc.rest.service.CalcService;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CalcServiceTest {

    @Mock
    private ReplyingKafkaTemplate<String, CalcRequest, CalcResponse> replyingKafkaTemplate;

    @Mock
    private RequestReplyFuture<String, CalcRequest, CalcResponse> replyFuture;

    @Mock
    private ConsumerRecord<String, CalcResponse> consumerRecord;

    @InjectMocks
    private CalcService calcService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(calcService, "requestsTopicName", "calc-requests");
        ReflectionTestUtils.setField(calcService, "responsesTopicName", "calc-responses");
    }

    @Test
    void shouldSendRequestAndReturnResponse() throws Exception {
        CalcResponse expectedResponse = new CalcResponse("uid-1", new BigDecimal("3"));

        when(replyingKafkaTemplate.sendAndReceive(any(ProducerRecord.class))).thenReturn(replyFuture);
        when(replyFuture.get(10L, TimeUnit.SECONDS)).thenReturn(consumerRecord);
        when(consumerRecord.value()).thenReturn(expectedResponse);

        CalcResponse result = calcService.sendRequest(new BigDecimal("1"), new BigDecimal("2"), '+');

        assertEquals(new BigDecimal("3"), result.getResult());
        verify(replyingKafkaTemplate, times(1)).sendAndReceive(any(ProducerRecord.class));
    }

    @Test
    void shouldThrowBadRequestOnDivisionByZero() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> calcService.sendRequest(new BigDecimal("10"), BigDecimal.ZERO, '/'));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Division by zero is not allowed.", exception.getReason());
        verifyNoInteractions(replyingKafkaTemplate);
    }

    @Test
    void shouldNotContactKafkaWhenDivisionByZero() {
        assertThrows(ResponseStatusException.class,
                () -> calcService.sendRequest(new BigDecimal("10"), BigDecimal.ZERO, '/'));
        verifyNoInteractions(replyingKafkaTemplate);
    }
}
