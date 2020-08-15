package com.app.bootstrapbugz.service.impl;

import com.app.bootstrapbugz.dto.request.admin.AdminRequest;
import com.app.bootstrapbugz.dto.request.admin.ChangeRoleRequest;
import com.app.bootstrapbugz.model.user.Role;
import com.app.bootstrapbugz.model.user.User;
import com.app.bootstrapbugz.repository.user.RoleRepository;
import com.app.bootstrapbugz.repository.user.UserRepository;
import com.app.bootstrapbugz.service.AdminService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void logoutUsersFromAllDevices(AdminRequest adminRequest) {
        List<User> users = userRepository.findAllByUsernameIn(adminRequest.getUsernames());
        users.forEach(User::updateLogoutFromAllDevicesAt);
        userRepository.saveAll(users);
    }

    @Override
    public void changeUsersRole(ChangeRoleRequest changeRoleRequest) {
        List<User> users = userRepository.findAllByUsernameIn(changeRoleRequest.getUsernames());
        users.forEach(user -> {
            List<Role> roles = roleRepository.findAllByNameIn(changeRoleRequest.getRoleNames());
            user.setRoles(new HashSet<>(roles));
            user.updateUpdatedAt();
        });
        userRepository.saveAll(users);
    }

    @Override
    public void lockUsers(AdminRequest adminRequest) {
        List<User> users = userRepository.findAllByUsernameIn(adminRequest.getUsernames());
        users.forEach(user -> {
            user.setNonLocked(false);
            user.updateUpdatedAt();
        });
        userRepository.saveAll(users);
    }

    @Override
    public void unlockUsers(AdminRequest adminRequest) {
        List<User> users = userRepository.findAllByUsernameIn(adminRequest.getUsernames());
        users.forEach(user -> {
            user.setNonLocked(true);
            user.updateUpdatedAt();
        });
        userRepository.saveAll(users);
    }

    @Override
    public void activateUser(AdminRequest adminRequest) {
        List<User> users = userRepository.findAllByUsernameIn(adminRequest.getUsernames());
        users.forEach(user -> {
            user.setActivated(true);
            user.updateUpdatedAt();
        });
        userRepository.saveAll(users);
    }

    @Override
    public void deactivateUser(AdminRequest adminRequest) {
        List<User> users = userRepository.findAllByUsernameIn(adminRequest.getUsernames());
        users.forEach(user -> {
            user.setActivated(false);
            user.updateUpdatedAt();
        });
        userRepository.saveAll(users);
    }

    @Override
    public void deleteUsers(AdminRequest adminRequest) {
        List<User> users = userRepository.findAllByUsernameIn(adminRequest.getUsernames());
        userRepository.deleteAll(users);
    }
}
