package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    public static final int USERS_PER_PAGE = 4;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> listAll() {
        return (List<User>) userRepository.findAll(Sort.by("id").ascending());
    }

    public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);
        if (keyword != null) {
            return userRepository.findAll(keyword, pageable);
        }
        return userRepository.findAll(pageable);
    }

    public List<Role> listRoles() {
        return (List<Role>) roleRepository.findAll();
    }

    public User save(User user) {
        boolean isEditMode = (user.getId() != null);
        //update user mode
        if (isEditMode) {
            User existingUser = userRepository.findById(user.getId()).get();
            if (user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                encodePassword(user);
            }
            // create user mode
        } else {
            encodePassword(user);
        }

        return userRepository.save(user);
    }

    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(Integer id, String email) {
        Optional<User> optionalUser = userRepository.getUserByEmail(email);
        //no user with such email exists
        if (optionalUser.isEmpty()) return true;
        //otherwise there's already user with this email
        //if create user mode
        if (id == null) {
            return false;
            //if update user mode
        } else {
            return Objects.equals(optionalUser.get().getId(), id);
        }
    }

    public User getUserById(Integer id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Could not find any user with ID " + id));
    }

    public void delete(Integer id) throws UserNotFoundException {
        Long countById = userRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new UserNotFoundException("Could not find any user with ID " + id);
        }
        userRepository.deleteById(id);
    }

    public void updateUserEnabledStatus(Integer id, boolean enabled) {
        userRepository.updateEnabledStatus(id, enabled);
    }
}
