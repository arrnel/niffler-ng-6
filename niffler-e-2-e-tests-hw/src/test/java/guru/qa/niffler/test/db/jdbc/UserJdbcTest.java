package guru.qa.niffler.test.db.jdbc;

import guru.qa.niffler.data.entity.auth.AuthUserJson;
import guru.qa.niffler.model.UserModel;
import guru.qa.niffler.service.impl.jdbc.AuthUserDbClientJdbc;
import guru.qa.niffler.service.impl.jdbc.UserDbClientJdbcXa;
import guru.qa.niffler.service.impl.jdbc.UserdataDbClientJdbc;
import guru.qa.niffler.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class UserJdbcTest {

    private final AuthUserDbClientJdbc authDbClient = new AuthUserDbClientJdbc();
    private final UserdataDbClientJdbc userdataDbClient = new UserdataDbClientJdbc();
    private final UserDbClientJdbcXa userDbClient = new UserDbClientJdbcXa();

    @Test
    void shouldCreateNewUserInTwoDbTest() {

        var user = UserUtils.generateUser();

        userDbClient.createUserInAuthAndUserdataDBs(user);

        assertAll("Users from niffler-auth and niffler-userdata should have id", () -> {
            assertNotNull(authDbClient
                    .findByUsername(user.getUsername())
                    .orElse(new AuthUserJson())
                    .getId());
            assertNotNull(userdataDbClient
                    .findByUsername(user.getUsername())
                    .orElse(new UserModel())
                    .getId());
        });

    }

    @Test
    void shouldDeleteUserFromTwoDbTest() {

        var user = UserUtils.generateUser();

        userDbClient.createUserInAuthAndUserdataDBs(user);
        userDbClient.deleteUserFromAuthAndUserdataDBs(user);

        assertAll("User should not exists in niffler-auth and niffler-userdata", () -> {
            assertTrue(authDbClient
                    .findByUsername(user.getUsername())
                    .isEmpty());
            assertTrue(userdataDbClient
                    .findByUsername(user.getUsername())
                    .isEmpty());
        });

    }

}
