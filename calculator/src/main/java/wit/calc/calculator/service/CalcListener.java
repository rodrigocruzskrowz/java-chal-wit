package wit.calc.calculator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import wit.calc.common.dto.CalcRequest;
import wit.calc.common.dto.CalcResponse;

import java.math.BigDecimal;
import java.math.MathContext;

@Service
public class CalcListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalcListener.class);

    @KafkaListener(topics = "${spring.kafka.topic.name.requests}", groupId = "${spring.kafka.consumer.group-id}")
    @SendTo("${spring.kafka.topic.name.responses}")
    public CalcResponse performCalc(CalcRequest request){

        MDC.put("uid", request.getUid());
        try{
            LOGGER.info("::::> Received calculation request: {} {} {}", request.getA(), request.getOperation(), request.getB());

            BigDecimal result = switch (request.getOperation()) {
                case '+' -> request.getA().add(request.getB());
                case '-' -> request.getA().subtract(request.getB());
                case '*' -> request.getA().multiply(request.getB());
                case '/' -> request.getA().divide(request.getB(), MathContext.DECIMAL128);
                default -> throw new IllegalArgumentException("Invalid operation: " + request.getOperation());
            };
            LOGGER.info("::::> Calculation result: {} {} {} = {}", request.getA(), request.getOperation(), request.getB(), result);

            return new CalcResponse(request.getUid(), result);
        } finally {
            MDC.clear();
        }
    }

}
