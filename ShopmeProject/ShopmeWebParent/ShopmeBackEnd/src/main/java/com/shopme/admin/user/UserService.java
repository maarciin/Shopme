package com.shopme.admin.user;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, USERS_PER_PAGE, userRepository);
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

    public User updateAccount(User userInForm) {
        User userInDb = userRepository.findById(userInForm.getId()).get();

        if (!userInForm.getPassword().isEmpty()) {
            userInDb.setPassword(userInForm.getPassword());
            encodePassword(userInDb);
        }

        if (userInForm.getPhotos() != null) {
            userInDb.setPhotos(userInForm.getPhotos());
        }

        userInDb.setFirstName(userInForm.getFirstName());
        userInDb.setLastName(userInForm.getLastName());

        return userRepository.save(userInDb);
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

    public User getByEmail(String email) throws UserNotFoundException {
        return userRepository.getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Could not find any user with email " + email));
    }
}
