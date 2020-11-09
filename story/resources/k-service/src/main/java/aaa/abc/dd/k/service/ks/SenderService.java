package aaa.abc.dd.k.service.ks;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static aaa.abc.dd.k.service.ks.SenderFactory.*;

public class SenderService implements Service {
    private final String bootstrapServers;
    private final String senderTopic;

    private final int senderTasksN = 1;
    private final int tasksN = 2;

    private final EmailService emailService;

    public SenderService(
            String bootstrapServers,
            String senderTopic,
            EmailService emailService
    ) {
        this.bootstrapServers = bootstrapServers;
        this.senderTopic = senderTopic;
        this.emailService = emailService;
    }

    @Override
    public void start() {
        Collection<AutoCloseable> closeables = new ArrayList<>();
        ExecutorService senderTasksExecutor = Executors.newFixedThreadPool(senderTasksN);
        ExecutorService tasksExecutorService = Executors.newFixedThreadPool(tasksN);
        for (int i = 0; i < senderTasksN; i++) {
            SenderConsumerLoop senderConsumerLoop =
                    new SenderConsumerLoop(
                            bootstrapServers,
                            senderTopic,
                            "sender",
                            "sender",
                            tasksExecutorService,
                            emailService
                    );
            closeables.add(senderConsumerLoop);
            senderTasksExecutor.submit(senderConsumerLoop);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (AutoCloseable autoCloseable : closeables) {
                try {
                    autoCloseable.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            senderTasksExecutor.shutdown();
            tasksExecutorService.shutdown();
            stop();
            try {
                senderTasksExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public void stop() {
    }

    static class SenderConsumerLoop implements Runnable, AutoCloseable {
        private final String bootstrapServers;
        private final String topic;
        private final String clientId;
        private final String groupId;

        KafkaConsumer<String, String> kafkaConsumer;

        private final ExecutorService tasksExecutorService;
        private final EmailService emailService;

        SenderConsumerLoop(
                String bootstrapServers,
                String topic,
                String clientId,
                String groupId,
                ExecutorService tasksExecutorService,
                EmailService emailService
        ) {
            this.bootstrapServers = bootstrapServers;
            this.topic = topic;
            this.clientId = clientId;
            this.groupId = groupId;
            this.tasksExecutorService = tasksExecutorService;
            this.emailService = emailService;
        }

        @Override
        public void run() {
            kafkaConsumer = createKafkaConsumerStringString(bootstrapServers, clientId, groupId);
            kafkaConsumer.subscribe(Collections.singleton(topic));
            while (true) {
                calculate(kafkaConsumer.poll(Duration.ofSeconds(1)));
            }
        }

        @Override
        public void close() {
        }

        void calculate(ConsumerRecords<String, String> records) {
            for (ConsumerRecord<String, String> record : records) {
                calculate(record);
            }
        }

        void calculate(ConsumerRecord<String, String> record) {
            JSONParser jsonParser = new JSONParser();
            Object parsedObject = null;
            try {
                parsedObject = jsonParser.parse(record.value());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (parsedObject instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) parsedObject;
                JSONObject jsonSubject = (JSONObject) jsonObject.get(SUBJECT);
                String subjectType = jsonSubject.get(SUBJECT_TYPE).toString();
                if (SEND.equals(subjectType)) {
                    JSONObject jsonBody = (JSONObject) jsonObject.get(BODY);
                    calculate(jsonBody);
                }
            }
        }

        void calculate(JSONObject jsonBody) {
            String method = jsonBody.get(METHOD).toString();
            if (EMAIL_METHOD.equals(method)) {
                String recipients = jsonBody.get(RECIPIENTS).toString();
                String title = jsonBody.get(TITLE).toString();
                String message = jsonBody.get(MESSAGE).toString();
                sendEmail(recipients, title, message);
            }
        }

        void sendEmail(String recipients, String title, String message) {
            tasksExecutorService.submit(() -> emailService.send(recipients, title, message));
        }

        static KafkaConsumer<String, String> createKafkaConsumerStringString(
                String bootstrapServers,
                String clientId,
                String groupId
        ) {
            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            properties.setProperty(
                    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                    "org.apache.kafka.common.serialization.StringDeserializer");
            properties.setProperty(
                    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                    "org.apache.kafka.common.serialization.StringDeserializer");
            properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            return new KafkaConsumer<>(properties);
        }
    }

    interface EmailService {
        void send(String recipients, String title, String message);
    }

    static class EmailServiceImpl implements EmailService {
        @Override
        public void send(String recipients, String title, String message) {
        }
    }

    static class EmailServicePrint implements EmailService {
        @Override
        public void send(String recipients, String title, String message) {
            System.out.println(recipients + ">" + title + ">" + message);
        }
    }

    public static void main(String[] args) {
        run(args);
    }

    static void run(String[] args) {
        String bootstrapServers = "localhost:9092";
        String topic = "sender_data";
        if (args.length > 1) {
            bootstrapServers = args[0];
            topic = args[1];
        }
        SenderService senderService = new SenderService(bootstrapServers, topic, new EmailServiceImpl());
        senderService.start();
    }
}
