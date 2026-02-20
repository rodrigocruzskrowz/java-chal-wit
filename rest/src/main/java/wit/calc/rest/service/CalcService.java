package wit.calc.rest.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import wit.calc.common.dto.CalcRequest;
import wit.calc.common.dto.CalcResponse;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CalcService {

    private final ReplyingKafkaTemplate<String, CalcRequest, CalcResponse> replyingKafkaTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(CalcService.class);

    @Value("${spring.kafka.topic.name.requests}")
    private String requestsTopicName;

    @Value("${spring.kafka.topic.name.responses}")
    private String responsesTopicName;

    public CalcService(ReplyingKafkaTemplate<String, CalcRequest, CalcResponse> replyingKafkaTemplate) {
        this.replyingKafkaTemplate = replyingKafkaTemplate;
    }

    public CalcResponse sendRequest(BigDecimal a, BigDecimal b, char o) throws Exception {
        CalcRequest request = new CalcRequest(UUID.randomUUID().toString(), o, a, b);
        if(o == '/' && b.compareTo(BigDecimal.ZERO) == 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Division by zero is not allowed.");
        }

        MDC.put("uid", request.getUid());
        try{
            LOGGER.info("::::> Sending calculation request: {} {} {}", request.getA(), request.getOperation(), request.getB());

            //Send to Kafka
            ProducerRecord<String, CalcRequest> record = new ProducerRecord<>(requestsTopicName, request);
            LOGGER.info("::::> Request sent to Kafka topic: {}", requestsTopicName);

            //Send and get promise for response
            RequestReplyFuture<String, CalcRequest, CalcResponse> future = replyingKafkaTemplate.sendAndReceive(record);
            LOGGER.info("::::> Awaiting response from Kafka topic: {}", responsesTopicName);

            //Await calculator module response
            ConsumerRecord<String, CalcResponse> consumerRecord = future.get(10, TimeUnit.SECONDS);
            LOGGER.info("::::> Received response from Kafka topic: {} with result: {}", responsesTopicName, consumerRecord.value().getResult());

            return consumerRecord.value();
        } finally {
            MDC.remove("uid");
        }
    }
}
