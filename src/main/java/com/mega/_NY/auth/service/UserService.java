package com.mega._NY.auth.service;

import com.mega._NY.auth.dto.UserDTO;
import com.mega._NY.auth.entity.AuthUtils;
import com.mega._NY.auth.entity.User;
import com.mega._NY.auth.entity.UserStatus;
import com.mega._NY.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtils authUtils;

    public User joinUser( User user ){
        existEmail(user.getEmail());
        existDisplayName(user.getNickName());
        encodePassword(user);
        existPhoneNum(user.getPhone());
        createRole(user);
        userRepository.save(user);
        return user;
    }

    private void createRole( User user ){
        List<String> roles = authUtils.createRoles();
        user.setRoles(roles);
    }

    private void existPhoneNum(String PhoneNum){
        Optional<User> user  = userRepository.findByPhone(PhoneNum);
        if(user.isPresent()) throw new RuntimeException();

    }

    private void encodePassword( User user ){
        String encodedPwd = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPwd);
    }

    private void existDisplayName( String diplayName ){
        Optional<User> user = userRepository.findByNickName(diplayName);
        if(user.isPresent()) throw new RuntimeException();//TODO : 예외처리 해야ㄴ
    }

    private void existEmail( String email ){
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()) throw new RuntimeException();

    }

    public User getLoginUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Optional<User> user = userRepository.findByEmail(name);
        return user.orElseThrow(() -> new RuntimeException("없어요"));
    }
}
