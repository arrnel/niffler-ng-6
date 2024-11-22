package guru.qa.niffler.test.fake.db.jdbc;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.impl.jdbc.AuthAuthorityDaoJdbc;
import guru.qa.niffler.model.rest.AuthUserJson;
import guru.qa.niffler.service.AuthUserClient;
import guru.qa.niffler.service.UserdataClient;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.db.impl.jdbc.UsersDbClientJdbc;
import guru.qa.niffler.service.db.impl.springJdbc.AuthUserDbClientSpringJdbc;
import guru.qa.niffler.service.db.impl.springJdbc.UserdataDbClientSpringJdbc;
import guru.qa.niffler.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.UserUtils.generateUser;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class UsersJdbcTest {

    private final UsersClient usersClient = new UsersDbClientJdbc();

    private final AuthAuthorityDao authorityDao = new AuthAuthorityDaoJdbc();
    private final AuthUserClient authUserClient = new AuthUserDbClientSpringJdbc();
    private final UserdataClient userdataClient = new UserdataDbClientSpringJdbc();

    @Test
    void shouldCreateNewUserInTwoDbTest() {

        var user = usersClient.createUser(UserUtils.generateUser());

        var authUser = authUserClient.findByUsername(user.getUsername());
        var authorities = authorityDao.findByUserId(authUser.orElse(new AuthUserJson()).getId());
        var userdataUser = userdataClient.findByUsername(user.getUsername());

        assertAll("Users from niffler-auth and niffler-userdata should exists and have authorities", () -> {
            assertEquals(2, authorities.size());
            assertTrue(authUser.isPresent());
            assertTrue(userdataUser.isPresent());
        });

    }

    @Test
    void shouldSendIncomeInvitationsTest() {
        var requester = userdataClient.create(generateUser());
        usersClient.getIncomeInvitationFromNewUsers(requester, 2);
    }

    @Test
    void shouldSendOutcomeInvitationsTest() {
        var requester = userdataClient.create(generateUser());
        usersClient.sendOutcomeInvitationToNewUsers(requester, 2);
    }

    @Test
    void shouldAddNewFriendsTest() {
        var requester = userdataClient.create(generateUser());
        usersClient.sendOutcomeInvitationToNewUsers(requester, 2);
    }

    @Test
    void shouldRemoveUserFromTwoDbTest() {

        var user = usersClient.createUser(UserUtils.generateUser());
        var authUser = authUserClient.findByUsername(user.getUsername()).orElse(new AuthUserJson());
        usersClient.removeUser(user);

        var authorities = authorityDao.findByUserId(authUser.getId());
        var authUserAfterDelete = authUserClient.findByUsername(user.getUsername());
        var userdataUser = userdataClient.findByUsername(user.getUsername());

        assertAll("User should not exists in niffler-auth and niffler-userdata", () -> {
            assertTrue(authUserAfterDelete.isEmpty());
            assertTrue(authorities.isEmpty());
            assertTrue(userdataUser.isEmpty());
        });

    }

}
