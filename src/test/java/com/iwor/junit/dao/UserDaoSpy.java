package com.iwor.junit.dao;

import java.util.HashMap;
import java.util.Map;

/**
 * примерно, что происходит в Mockito
 */
public class UserDaoSpy extends UserDao {
    private final UserDao userDao;
    private final Map<Integer, Boolean> answers = new HashMap<>();

    public UserDaoSpy(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean deleteById(Integer id) {
        return answers.getOrDefault(id, userDao.deleteById(id));
    }
}
