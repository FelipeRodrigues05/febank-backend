package com.spring.bank.domain.service;

import com.spring.bank.common.exception.InvalidPasswordException;
import com.spring.bank.common.exception.UserAlreadyHasAccountException;
import com.spring.bank.common.exception.UserNotFoundException;
import com.spring.bank.common.utils.PasswordEncrypt;
import com.spring.bank.domain.dto.user.LoginDTO;
import com.spring.bank.domain.dto.user.RegisterDTO;
import com.spring.bank.domain.model.User;
import com.spring.bank.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private UserRepository userRepository;
    private PasswordEncrypt passwordEncrypt;

    private UserService userService;

    public User register(RegisterDTO data) throws UserAlreadyHasAccountException {
        if (this.userRepository.existsByDocument(data.document())) {
            throw new UserAlreadyHasAccountException(
                    String.format("DOCUMENT %s already exists", data.document())
            );
        }

        // GENERATE TOKEN -> TOKEN_SERVICE

        return userService.create(data);
    }

    public User login(LoginDTO data) throws UserNotFoundException, InvalidPasswordException {
        User user = this.userRepository.findByDocument(data.document()).orElseThrow(() -> new UserNotFoundException(
                String.format("User with DOCUMENT %s not found", data.document())
        ));

        if(!passwordEncrypt.verifyPassword(user.getPassword(), data.password())) {
            throw new InvalidPasswordException("Invalid password");
        }

        // RETRIEVE TOKEN -> TOKEN_SERVICE

        return user;
    }
}
