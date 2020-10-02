package com.app.bootstrapbugz.service.impl;

import com.app.bootstrapbugz.constant.JwtPurpose;
import com.app.bootstrapbugz.dto.model.user.UserDto;
import com.app.bootstrapbugz.dto.request.user.ChangePasswordRequest;
import com.app.bootstrapbugz.dto.request.user.EditUserRequest;
import com.app.bootstrapbugz.constant.ErrorDomain;
import com.app.bootstrapbugz.error.exception.BadRequestException;
import com.app.bootstrapbugz.error.exception.ResourceNotFound;
import com.app.bootstrapbugz.event.OnSendJwtEmail;
import com.app.bootstrapbugz.hal.user.UserDtoModelAssembler;
import com.app.bootstrapbugz.model.user.User;
import com.app.bootstrapbugz.repository.user.UserRepository;
import com.app.bootstrapbugz.security.jwt.JwtUtilities;
import com.app.bootstrapbugz.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.hateoas.CollectionModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final UserDtoModelAssembler assembler;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final JwtUtilities jwtUtilities;

    public UserServiceImpl(UserRepository userRepository, MessageSource messageSource, UserDtoModelAssembler assembler,
                           PasswordEncoder bCryptPasswordEncoder, ApplicationEventPublisher eventPublisher, JwtUtilities jwtUtilities) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
        this.assembler = assembler;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.eventPublisher = eventPublisher;
        this.jwtUtilities = jwtUtilities;
    }

    @Override
    public CollectionModel<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty())
            throw new ResourceNotFound(messageSource.getMessage("users.notFound", null, LocaleContextHolder.getLocale()), ErrorDomain.USER);
        return assembler.toCollectionModel(map(users));
    }

    private CollectionModel<UserDto> map(List<User> users) {
        ModelMapper modelMapper = new ModelMapper();
        Collection<UserDto> collection = new ArrayList<>();
        for (User user : users) {
            collection.add(modelMapper.map(user, UserDto.class));
        }
        return CollectionModel.of(collection);
    }

    @Override
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale()), ErrorDomain.USER));
        return assembler.toModel(new ModelMapper().map(user, UserDto.class));
    }

    @Override
    public UserDto edit(EditUserRequest editUserRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale()), ErrorDomain.USER));
        user.setFirstName(editUserRequest.getFirstName());
        user.setLastName(editUserRequest.getLastName());
        tryToSetUsername(user, editUserRequest.getUsername());
        tryToSetEmail(user, editUserRequest.getEmail());
        return assembler.toModel(new ModelMapper().map(userRepository.save(user), UserDto.class));
    }

    private void tryToSetUsername(User user, String username) {
        if (user.getUsername().equals(username))
            return;
        if (userRepository.existsByUsername(username))
            throw new BadRequestException(messageSource.getMessage("username.exists", null, LocaleContextHolder.getLocale()), ErrorDomain.USER);

        user.setUsername(username);
        user.updateUpdatedAt();
    }

    private void tryToSetEmail(User user, String email) {
        if (user.getEmail().equals(email))
            return;
        if (userRepository.existsByEmail(email))
            throw new BadRequestException(messageSource.getMessage("email.exists", null, LocaleContextHolder.getLocale()), ErrorDomain.USER);

        user.setEmail(email);
        user.setActivated(false);
        user.updateUpdatedAt();

        String token = jwtUtilities.createToken(user, JwtPurpose.CONFIRM_REGISTRATION);
        eventPublisher.publishEvent(new OnSendJwtEmail(user, token, JwtPurpose.CONFIRM_REGISTRATION));
    }

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale()), ErrorDomain.USER));
        if (!bCryptPasswordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword()))
            throw new BadRequestException(messageSource.getMessage("changePassword.badOldPassword", null, LocaleContextHolder.getLocale()), ErrorDomain.USER);
        changePassword(user, changePasswordRequest.getNewPassword());
    }

    private void changePassword(User user, String password) {
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.updateUpdatedAt();
        userRepository.save(user);
    }

    @Override
    public void logoutFromAllDevices() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale()), ErrorDomain.USER));
        user.updateLogoutFromAllDevicesAt();
        userRepository.save(user);
    }
}
