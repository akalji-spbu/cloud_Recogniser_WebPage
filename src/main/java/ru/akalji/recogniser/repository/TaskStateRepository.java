package ru.akalji.recogniser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akalji.recogniser.model.Role;
import ru.akalji.recogniser.model.TaskState;

@Repository("taskStateRepository")
public interface TaskStateRepository extends JpaRepository<TaskState, Long>{
    TaskState findByName(String state_name);

}