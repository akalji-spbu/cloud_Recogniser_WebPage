package ru.akalji.recogniser.service;

import ru.akalji.recogniser.model.User;

public interface UserService {
    public User findUserByEmail(String email);
    public void saveUser(User user);
}