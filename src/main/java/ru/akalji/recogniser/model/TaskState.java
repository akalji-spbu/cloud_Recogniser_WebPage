package ru.akalji.recogniser.model;

import javax.persistence.*;
import java.util.Optional;

@Entity
@Table(name = "task_states")
public class TaskState {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "state_id")
    private Long id;

    @Column(name = "state_name")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
