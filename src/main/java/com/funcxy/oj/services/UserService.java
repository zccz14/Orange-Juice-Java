package com.funcxy.oj.services;

import com.funcxy.oj.repositories.UserRepository;
import com.funcxy.oj.models.User;
import com.funcxy.oj.utils.InvalidException;
import com.funcxy.oj.utils.UserUtil;
import com.funcxy.oj.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by aak12 on 2017/2/28.
 */
@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    public void save(User user) throws InvalidException{
        user.setUsername(user.getUsername().trim());
        user.setEmail(user.getEmail().trim());
        if (Validation.userNotValid(user))throw new InvalidException();
        user.passwordEncrypt();
        userRepository.save(user);
    }
}
