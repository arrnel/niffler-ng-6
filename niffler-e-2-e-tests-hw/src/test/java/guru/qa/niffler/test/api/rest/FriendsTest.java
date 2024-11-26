package guru.qa.niffler.test.api.rest;

import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.api.core.store.AuthStore;
import guru.qa.niffler.api.gateway.v1.UserdataV1ApiClientRetrofit;
import guru.qa.niffler.enums.FriendState;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.CreateNewUser;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.model.rest.UserJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RestTest
class FriendsTest {

    private final UserdataV1ApiClientRetrofit userdataClient = new UserdataV1ApiClientRetrofit();

    @Test
    void allFriendsAndIncomeInvitationsShouldBeReturnedFroUser(
            @ApiLogin
            @CreateNewUser(
                    friends = 2,
                    incomeInvitations = 1
            )
            UserJson user
    ) {
        final List<UserJson> expectedFriends = user.getTestData().getFriends();
        final List<UserJson> expectedInvitations = user.getTestData().getIncomeInvitations();

        final List<UserJson> result = userdataClient.allFriends(
                user.getTestData().getToken(),
                null
        );

        Assertions.assertNotNull(result);
        Assertions.assertEquals(3, result.size());

        final List<UserJson> friendsFromResponse = result.stream().filter(
                u -> u.getFriendState() == FriendState.FRIEND
        ).toList();

        final List<UserJson> invitationsFromResponse = result.stream().filter(
                u -> u.getFriendState() == FriendState.INVITE_RECEIVED
        ).toList();

        Assertions.assertEquals(2, friendsFromResponse.size());
        Assertions.assertEquals(1, invitationsFromResponse.size());

        Assertions.assertEquals(
                expectedInvitations.getFirst().getUsername(),
                invitationsFromResponse.getFirst().getUsername()
        );

        final UserJson firstUserFromRequest = friendsFromResponse.getFirst();
        final UserJson secondUserFromRequest = friendsFromResponse.getLast();

        Assertions.assertEquals(
                expectedFriends.getFirst().getUsername(),
                firstUserFromRequest.getUsername()
        );

        Assertions.assertEquals(
                expectedFriends.getLast().getUsername(),
                secondUserFromRequest.getUsername()
        );
    }

    @Test
    void shouldExists2FriendRecordsAfterAcceptInvitationTest(

            @CreateNewUser
            @ApiLogin
            UserJson user,

            @CreateNewUser
            @ApiLogin
            UserJson target

    ) {

        // Data
        var userToken = user.getTestData().getToken();
        var targetToken = target.getTestData().getToken();

        // Steps
        var sendInvitation = userdataClient.sendInvitation(
                targetToken,
                user.getUsername());

        var acceptInvitation = userdataClient.acceptInvitation(
                userToken,
                target.getUsername());

        var userFriends = userdataClient.allFriends(
                userToken,
                target.getUsername());

        var targetFriends = userdataClient.allFriends(
                targetToken,
                user.getUsername());

        assertAll("Both users have each other as friends after accept invitation",

                () -> assertEquals(FriendState.INVITE_SENT, sendInvitation.getFriendState()),

                () -> assertEquals(FriendState.FRIEND, acceptInvitation.getFriendState()),

                () -> assertTrue(userFriends.stream()
                        .anyMatch(tu ->
                                tu.getUsername().equals(target.getUsername()) &&
                                        tu.getFriendState() == FriendState.FRIEND
                        )),

                () -> assertTrue(targetFriends.stream()
                        .anyMatch(u ->
                                u.getUsername().equals(user.getUsername()) &&
                                        u.getFriendState() == FriendState.FRIEND))
        );

    }

    @Test
    void shouldNotExistFriendRecordsAfterDeclineInvitationTest(

            @CreateNewUser
            @ApiLogin
            UserJson user,

            @CreateNewUser
            @ApiLogin
            UserJson target

    ) {

        // Data
        var userToken = user.getTestData().getToken();
        var targetToken = target.getTestData().getToken();

        // Steps
        var sendInvitation = userdataClient.sendInvitation(
                targetToken,
                user.getUsername());

        var declineInvitation = userdataClient.declineInvitation(
                userToken,
                target.getUsername());

        var userFriends = userdataClient.allFriends(
                userToken,
                target.getUsername());

        var targetFriends = userdataClient.allFriends(
                targetToken,
                user.getUsername());

        assertAll("Both users should not have each other as friends after decline invitation",

                () -> assertEquals(FriendState.INVITE_SENT, sendInvitation.getFriendState()),

                () -> assertNull(declineInvitation.getFriendState()),

                () -> assertTrue(userFriends.stream()
                        .noneMatch(u ->
                                u.getUsername().equals(target.getUsername()))),
                () -> assertTrue(targetFriends.stream()
                        .noneMatch(u ->
                                u.getUsername().equals(user.getUsername())))
        );

    }

    @Test
    void shouldNotExistFriendRecordsAfterRemoveFriendTest(

            @CreateNewUser(
                    friends = 1
            )
            @ApiLogin
            UserJson user,

            @CreateNewUser
            @ApiLogin
            UserJson target

    ) {

        // Data
        var userToken = user.getTestData().getToken();
        var targetToken = target.getTestData().getToken();

        // Steps
        userdataClient.sendInvitation(
                targetToken,
                user.getUsername());

        userdataClient.acceptInvitation(
                userToken,
                target.getUsername());

        userdataClient.removeFriend(
                userToken,
                target.getUsername());

        var userFriends = userdataClient.allFriends(
                userToken,
                target.getUsername());

        var targetFriends = userdataClient.allFriends(
                targetToken,
                user.getUsername());

        assertAll("Both users should not have each other as friends after friend removed",
                () -> assertTrue(userFriends.stream()
                        .noneMatch(tu ->
                                tu.getUsername().equals(target.getUsername()))),
                () -> assertTrue(targetFriends.stream()
                        .noneMatch(u ->
                                u.getUsername().equals(user.getUsername())))
        );

    }

}
