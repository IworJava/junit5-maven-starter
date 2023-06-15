package com.iwor.junit.dao;

import java.util.HashMap;
import java.util.Map;

/**
 * примерно, что происходит в Mockito
 */
public class UserDaoMock extends UserDao {
    private final Map<Integer, Boolean> answers = new HashMap<>();

    @Override
    public boolean deleteById(Integer id) {
        return answers.getOrDefault(id, false);
    }
}
