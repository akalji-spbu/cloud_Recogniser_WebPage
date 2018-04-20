package ru.akalji.recogniser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akalji.recogniser.model.Task;
import ru.akalji.recogniser.model.User;

import java.util.List;

@Repository("taskRepository")
public interface TaskRepository extends JpaRepository<Task, Long>{
    List<Task> findAllByUser(User user);
}