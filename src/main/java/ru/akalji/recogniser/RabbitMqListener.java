package ru.akalji.recogniser;


import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.akalji.recogniser.model.Task;
import ru.akalji.recogniser.model.TaskState;
import ru.akalji.recogniser.repository.TaskRepository;
import ru.akalji.recogniser.repository.TaskStateRepository;

@Component
public class RabbitMqListener {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStateRepository taskStateRepository;


    @RabbitListener(queues = "solved")
    public void worker1(String message) {
        ContentTypeDelegatingMessageConverter messageConverter = new ContentTypeDelegatingMessageConverter();
        JSONObject obj=(JSONObject) JSONValue.parse(message);
        Long task_id = Long.parseLong((String) obj.get("task"));
        String result = (String) obj.get("result");
        Double score = (Double) obj.get("score");
        try {
            System.out.println(task_id);
            TaskState taskState = taskStateRepository.findByName("Solved");
            Task task = taskRepository.getOne(task_id);
            task.setState(taskState);
            task.setResult("At " + String.format("%.2f", score) + "% this is the \n"+result);
            System.out.println(task.getId()+" "+task.getResult());
            taskRepository.save(task);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}