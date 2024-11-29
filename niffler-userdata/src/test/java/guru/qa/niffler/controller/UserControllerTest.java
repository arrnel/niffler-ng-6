package guru.qa.niffler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import guru.qa.niffler.data.CurrencyValues;
import guru.qa.niffler.data.UserEntity;
import guru.qa.niffler.data.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    private static final Faker FAKER = new Faker();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository usersRepository;

    @Test
    void currentUserEndpoint() throws Exception {
        UserEntity userDataEntity = new UserEntity();
        userDataEntity.setUsername("dima");
        userDataEntity.setCurrency(CurrencyValues.RUB);
        usersRepository.save(userDataEntity);

        mockMvc.perform(get("/internal/users/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "dima")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("dima"));
    }

    @Test
    void allUsersTest() throws Exception {

        UserEntity user1 = new UserEntity();
        user1.setUsername(FAKER.name().username());
        user1.setCurrency(CurrencyValues.RUB);
        usersRepository.save(user1);

        UserEntity user2 = new UserEntity();
        user2.setUsername(FAKER.name().username());
        user2.setCurrency(CurrencyValues.RUB);
        usersRepository.save(user2);

        mockMvc.perform(get("/internal/users/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", user1.getUsername())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].username").value(
                        hasItems(
                                user1.getUsername(),
                                user2.getUsername())));
    }

    @Test
    void allUsersWithSearchQueryTest() throws Exception {

        String baseUsername = FAKER.name().username();
        String usersIncludedBaseUsername = FAKER + baseUsername + "_";

        UserEntity notIncludedUser = new UserEntity();
        notIncludedUser.setUsername(baseUsername);
        notIncludedUser.setCurrency(CurrencyValues.RUB);
        usersRepository.save(notIncludedUser);

        UserEntity includedUser1 = new UserEntity();
        includedUser1.setUsername("_" + usersIncludedBaseUsername + FAKER.number().digits(3));
        includedUser1.setCurrency(CurrencyValues.RUB);
        usersRepository.save(includedUser1);

        UserEntity includedUser2 = new UserEntity();
        includedUser2.setUsername("." + usersIncludedBaseUsername + FAKER.number().digits(3));
        includedUser2.setCurrency(CurrencyValues.RUB);
        usersRepository.save(includedUser2);

        mockMvc.perform(get("/internal/users/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", notIncludedUser.getUsername())
                        .param("searchQuery", usersIncludedBaseUsername)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].username").value(
                        not(hasItem(
                                notIncludedUser.getUsername()))))
                .andExpect(jsonPath("$[*].username").value(
                        hasItems(
                                includedUser1.getUsername(),
                                includedUser2.getUsername())));
    }

    @Test
    void updateUserTest() throws Exception {

        UserEntity user = new UserEntity();
        user.setUsername(new Faker().name().username());
        user.setCurrency(CurrencyValues.RUB);
        usersRepository.save(user);

        user.setCurrency(CurrencyValues.KZT);

        mockMvc.perform(post("/internal/users/update")
                        .content(new ObjectMapper().writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.username == '" + user.getUsername() + "')].currency").value(CurrencyValues.KZT.name()));
    }

}