package guru.qa.niffler.api.gateway.v1;

import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.rest.FriendJson;
import guru.qa.niffler.model.rest.UserJson;
import io.qameta.allure.Step;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class UserdataV1ApiClientRetrofit extends RestClient {

    private final UserdataV1Api userdataApi;

    public UserdataV1ApiClientRetrofit() {
        super(CFG.gatewayUrl(), HttpLoggingInterceptor.Level.BODY);
        this.userdataApi = create(UserdataV1Api.class);
    }

    @Step("Send request GET:[niffler-gateway]/api/friends/all")
    public List<UserJson> allFriends(String bearerToken,
                                     @Nullable String searchQuery) {
        final Response<List<UserJson>> response;
        try {
            response = userdataApi.allFriends(
                            "Bearer " + bearerToken,
                            searchQuery)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("Send request POST:[niffler-gateway]/api/invitations/send")
    public UserJson sendInvitation(String bearerToken,
                                     String username) {
        final Response<UserJson> response;
        try {
            response = userdataApi.sendInvitation(
                            "Bearer " + bearerToken,
                            new FriendJson(username))
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("Send request POST:[niffler-gateway]/api/invitations/accept")
    public UserJson acceptInvitation(String bearerToken,
                                     String username) {
        final Response<UserJson> response;
        try {
            response = userdataApi.acceptInvitation(
                            "Bearer " + bearerToken,
                            new FriendJson(username))
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("Send request POST:[niffler-gateway]/api/invitations/decline")
    public UserJson declineInvitation(String bearerToken,
                                      String username) {
        final Response<UserJson> response;
        try {
            response = userdataApi.declineInvitation(
                            "Bearer " + bearerToken,
                            new FriendJson(username))
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("Send request POST:[niffler-gateway]/api/invitations/decline")
    public void removeFriend(String bearerToken,
                             String username) {
        final Response<Void> response;
        try {
            response = userdataApi.removeFriend(
                            "Bearer " + bearerToken,
                            username)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

}
