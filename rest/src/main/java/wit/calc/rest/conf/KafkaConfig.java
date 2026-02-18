package wit.calc.rest.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import wit.calc.common.dto.CalcRequest;
import wit.calc.common.dto.CalcResponse;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.topic.name.responses}")
    private String responsesTopicName;

    @Value("${spring.kafka.topic.name.requests}")
    private String requestsTopicName;

//    @Bean
//    public NewTopic responsesTopic() {
//        return TopicBuilder.name(responsesTopicName).build();
//    }
//
//    @Bean
//    public NewTopic requestsTopic() {
//        return TopicBuilder.name(requestsTopicName).build();
//    }

    @Bean
    public ReplyingKafkaTemplate<String, CalcRequest, CalcResponse> replyingKafkaTemplate(
            ProducerFactory<String, CalcRequest> pf,
            ConcurrentMessageListenerContainer<String, CalcResponse> repliesContainer) {
        return new ReplyingKafkaTemplate<>(pf, repliesContainer);
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, CalcResponse> repliesContainer(
            ConsumerFactory<String, CalcResponse> cf) {
        ContainerProperties containerProperties = new ContainerProperties(responsesTopicName);
        return new ConcurrentMessageListenerContainer<>(cf, containerProperties);
    }
}
