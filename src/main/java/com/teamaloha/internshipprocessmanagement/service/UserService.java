package com.teamaloha.internshipprocessmanagement.service;

import com.teamaloha.internshipprocessmanagement.dao.UserDao;
import com.teamaloha.internshipprocessmanagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User findByMail(String mail) {
        return userDao.findByMail(mail);
    }

    public boolean existsByMail(String mail) {
        return userDao.existsByMail(mail);
    }


}
