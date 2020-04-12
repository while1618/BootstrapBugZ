package com.app.webapp.service.impl;

import com.app.webapp.dto.model.UserDto;
import com.app.webapp.error.ErrorDomains;
import com.app.webapp.error.exception.ResourceNotFound;
import com.app.webapp.hal.UserDtoModelAssembler;
import com.app.webapp.model.User;
import com.app.webapp.repository.UserRepository;
import com.app.webapp.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final UserDtoModelAssembler assembler;

    public UserServiceImpl(UserRepository userRepository, MessageSource messageSource, UserDtoModelAssembler assembler) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
        this.assembler = assembler;
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
}
