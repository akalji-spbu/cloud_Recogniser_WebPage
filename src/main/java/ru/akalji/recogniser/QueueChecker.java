package ru.akalji.recogniser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import ru.akalji.recogniser.model.Task;
import ru.akalji.recogniser.model.TaskState;
import ru.akalji.recogniser.repository.TaskRepository;
import ru.akalji.recogniser.repository.TaskStateRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class QueueChecker implements Runnable {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStateRepository taskStateRepository;

    @Override
    public void run() {

            ConnectionFactory factory = new ConnectionFactory();
            factory.setUsername("website");
            factory.setPassword("z8swu5xxybmyk");
            factory.setVirtualHost("recogniser");
            factory.setHost("recogniser.akalji.ru");
            factory.setPort(5672);

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare("solved", false, false, false, null);
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    JSONObject obj=(JSONObject) JSONValue.parse(message);
                    Integer task_id = Integer.parseInt((String) obj.get("task"));
                    String result = (String) obj.get("result");
                    Double score = (Double) obj.get("score");
                    try {
                        System.out.println(task_id);
                        List<Task> task = taskRepository.findAll();
                        for (Task t:task) {
                            System.out.println(t.getId());
                        }
//                        TaskState taskState = taskStateRepository.findByName("Solved");
//                        task.setState(taskState);
//                        task.setResult("At "+String.format("%.2f", score)+"% this is the \n"+result);
//                        System.out.println(task.getId()+" "+task.getResult());
//                        taskRepository.save(task);
                    }catch (Exception e){
                        e.printStackTrace();
                    }




                }
            };
            channel.basicConsume("solved", true, consumer);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }
}
