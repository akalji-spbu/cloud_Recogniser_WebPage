package ru.akalji.recogniser.storage;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.akalji.recogniser.model.Task;
import ru.akalji.recogniser.model.TaskState;
import ru.akalji.recogniser.model.User;
import ru.akalji.recogniser.repository.TaskRepository;
import ru.akalji.recogniser.repository.TaskStateRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStateRepository taskStateRepository;

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file, User user) {
        Task task = new Task();
        TaskState taskState = taskStateRepository.getOne(2L);
        task.setUser(user);
        task.setState(taskState);
        taskRepository.save(task);

        Task test = taskRepository.getOne(task.getId());
        System.out.println("Тест: "+test.getId());


        try {

            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }
            Files.copy(file.getInputStream(), this.rootLocation.resolve(task.getId()+".jpg"));
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }

        HashMap<String,String> data = new HashMap<>();
        data.put("task",task.getId().toString());

        JSONObject json = new JSONObject(data);
        String message = json.toJSONString();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("website");
        factory.setPassword("z8swu5xxybmyk");
        factory.setVirtualHost("recogniser");
        factory.setHost("recogniser.akalji.ru");
        factory.setPort(5672);

        Connection connection = null;
        try {
            connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare("toSolve", false, false, false, null);
            channel.basicPublish("", "toSolve", null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(path -> this.rootLocation.relativize(path));
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

//    @Override
//    public void deleteAll() {
//        FileSystemUtils.deleteRecursively(rootLocation.toFile());
//    }

    @Override
    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            //throw new StorageException("Could not initialize storage", e);
            System.out.println("Directory alredy exists");
        }
    }
}