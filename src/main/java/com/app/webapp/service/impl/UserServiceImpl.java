package com.app.webapp.service.impl;

import com.app.webapp.dto.model.UserDto;
import com.app.webapp.dto.request.ChangePasswordRequest;
import com.app.webapp.dto.request.SignUpRequest;
import com.app.webapp.error.ErrorDomains;
import com.app.webapp.error.exception.BadRequestException;
import com.app.webapp.error.exception.ResourceNotFound;
import com.app.webapp.hal.UserDtoModelAssembler;
import com.app.webapp.model.Role;
import com.app.webapp.model.RoleName;
import com.app.webapp.model.User;
import com.app.webapp.repository.RoleRepository;
import com.app.webapp.repository.UserRepository;
import com.app.webapp.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.hateoas.CollectionModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final UserDtoModelAssembler assembler;
    private final PasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository, MessageSource messageSource, UserDtoModelAssembler assembler,
                           PasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
        this.assembler = assembler;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public CollectionModel<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty())
            throw new ResourceNotFound(messageSource.getMessage("users.notFound", null, LocaleContextHolder.getLocale()), ErrorDomains.USER);
        return assembler.toCollectionModel(map(users));
    }

    private CollectionModel<UserDto> map(List<User> users) {
        ModelMapper modelMapper = new ModelMapper();
        Collection<UserDto> collection = new ArrayList<>();
        for (User user: users) {
            collection.add(modelMapper.map(user, UserDto.class));
        }
        return new CollectionModel<>(collection);
    }

    @Override
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale()), ErrorDomains.USER));
        return assembler.toModel(new ModelMapper().map(user, UserDto.class));
    }

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale()), ErrorDomains.USER));
        if (!bCryptPasswordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword()))
            throw new BadRequestException(messageSource.getMessage("changePassword.badOldPassword", null, LocaleContextHolder.getLocale()), ErrorDomains.USER);
       changePassword(user, changePasswordRequest.getNewPassword());
    }

    private void changePassword(User user, String password) {
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void logoutFromAllDevices() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale()), ErrorDomains.USER));
        user.setLogoutFromAllDevicesAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
