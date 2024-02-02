package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateNewUserWithOneRole() {
        Role roleAdmin = entityManager.find(Role.class, 1);
        User userMarcin = new User("gawlikmr@gmail.com", "1234", "Marcin", "Gawlik");
        userMarcin.addRole(roleAdmin);
        User savedUser = userRepository.save(userMarcin);

        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateNewUserWithTwoRoles() {
        User userFranek = new User("franek@gmail.com", "franek123", "Franciszek", "Nowak");
        Role roleEditor = new Role(3);
        Role roleAssistant = new Role(5);
        userFranek.addRole(roleEditor);
        userFranek.addRole(roleAssistant);

        User savedUser = userRepository.save(userFranek);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllUsers() {
        Iterable<User> listUsers = userRepository.findAll();
        listUsers.forEach(System.out::println);
    }

    @Test
    public void testGetUserById() {
        Optional<User> user = userRepository.findById(1);
        user.ifPresent(System.out::println);
        assertThat(user).isPresent();
    }

    @Test
    public void testUpdateUserDetails() {
        Optional<User> oUser = userRepository.findById(1);
        oUser.ifPresent(user -> {
            user.setEnabled(true);
            user.setEmail("gawlikmarcin@gmail.com");
            userRepository.save(user);
        });
    }

    @Test
    public void testUpdateUserRoles() {
        Optional<User> oUserFranek = userRepository.findById(2);
        oUserFranek.ifPresent(userFranek -> {
            Role roleEditor = new Role(3);
            Role roleSalesPerson = new Role(2);
            userFranek.getRoles().remove(roleEditor);
            userFranek.addRole(roleSalesPerson);
            userRepository.save(userFranek);
        });
    }

    @Test
    public void testDeleteUser() {
        Integer userId = 2;
        userRepository.deleteById(userId);

        Optional<User> oUser = userRepository.findById(userId);
        assertThat(oUser).isEmpty();
    }

    @Test
    public void testGetUserByEmail() {
        String email = "gawlikmarcin@gmail.com";
        Optional<User> oUser = userRepository.getUserByEmail(email);
        assertThat(oUser).isPresent();
    }

    @Test
    public void testCountById() {
        Integer id = 1;
        Long count = userRepository.countById(id);
        assertThat(count).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testDisableUser() {
        Integer id = 1;
        userRepository.updateEnabledStatus(id, false);
    }

    @Test
    public void testEnableUser() {
        Integer id = 1;
        userRepository.updateEnabledStatus(id, true);
    }

    @Test
    public void testListFirstPage() {
        int pageNumber = 3;
        int pageSize = 5;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = userRepository.findAll(pageable);
        List<User> listUsers = page.getContent();
        listUsers.forEach(System.out::println);

        assertThat(listUsers.size()).isEqualTo(pageSize);
    }

    @Test
    public void testSearchUsers() {
        String keyword = "bruce";
        int pageNumber = 0;
        int pageSize = 4;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = userRepository.findAll(keyword, pageable);
        List<User> listUsers = page.getContent();
        listUsers.forEach(System.out::println);

        assertThat(listUsers.size()).isGreaterThan(0);
    }
}