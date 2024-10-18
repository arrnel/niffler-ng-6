package guru.qa.niffler.test.db.springJdbc;

import guru.qa.niffler.data.entity.auth.AuthAuthorityJson;
import guru.qa.niffler.data.entity.auth.AuthUserJson;
import guru.qa.niffler.model.UserModel;
import guru.qa.niffler.service.impl.jdbc.AuthUserDbClientJdbc;
import guru.qa.niffler.service.impl.jdbc.UserdataDbClientJdbc;
import guru.qa.niffler.service.impl.springJdbc.AuthAuthorityDbClientSpringJdbc;
import guru.qa.niffler.service.impl.springJdbc.AuthUserDbClientSpringJdbc;
import guru.qa.niffler.service.impl.springJdbc.UserDbClientSpringJdbc;
import guru.qa.niffler.service.impl.springJdbc.UserdataDbClientSpringJdbc;
import guru.qa.niffler.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class UserSpringJdbcTest {


    @Test
    void shouldCreateNewUserInTwoDbTest() {

        AuthUserDbClientSpringJdbc authUserDbClient = new AuthUserDbClientSpringJdbc();
        var user = UserUtils.generateUser();

        new UserDbClientSpringJdbc().createUserInAuthAndUserdataDBs(user);

        assertAll("Users from niffler-auth and niffler-userdata should have id", () -> {
            assertNotNull(new AuthUserDbClientJdbc()
                    .findByUsername(user.getUsername())
                    .orElse(new AuthUserJson())
                    .getId());
            assertEquals(2,
                    new AuthAuthorityDbClientSpringJdbc()
                            .findByUserId(authUserDbClient
                                    .findByUsername(user.getUsername())
                                    .orElse(new AuthUserJson())
                                    .getId())
                            .size());
            assertNotNull(new UserdataDbClientSpringJdbc()
                    .findByUsername(user.getUsername())
                    .orElse(new UserModel())
                    .getId());
        });

    }

    @Test
    void shouldDeleteUserFromTwoDbTest() {

        UserDbClientSpringJdbc userDbClient = new UserDbClientSpringJdbc();
        var user = UserUtils.generateUser();

        userDbClient.createUserInAuthAndUserdataDBs(user);
        userDbClient.deleteUserFromAuthAndUserdataDBs(user);

        List<AuthAuthorityJson> authorities = new AuthAuthorityDbClientSpringJdbc().findByUserId(user.getId());
        Optional<AuthUserJson> authUser = new AuthUserDbClientSpringJdbc().findByUsername(user.getUsername());
        Optional<UserModel> userdataUser = new UserdataDbClientJdbc().findByUsername(user.getUsername());

        assertAll("User should not exists in niffler-auth and niffler-userdata", () -> {
            assertTrue(authorities.isEmpty());
            assertTrue(authUser.isEmpty());
            assertTrue(userdataUser.isEmpty());
        });

    }
}
