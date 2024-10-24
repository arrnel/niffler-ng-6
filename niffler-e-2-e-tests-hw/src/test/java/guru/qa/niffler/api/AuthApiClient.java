package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.enums.HttpStatus;
import guru.qa.niffler.enums.Token;
import guru.qa.niffler.mapper.LoginModelToMap;
import guru.qa.niffler.mapper.RegisterModelMapper;
import guru.qa.niffler.model.LoginModel;
import guru.qa.niffler.model.RegisterModel;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.Assertions;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Slf4j
public class AuthApiClient {

    private static final RegisterModelMapper registerModelToMap = new RegisterModelMapper();
    private static final LoginModelToMap loginModelToMap = new LoginModelToMap();
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Config.getInstance().authUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .client(new OkHttpClient.Builder()
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .followRedirects(false)
                    .build())
            .build();
    private final AuthApi authApi = retrofit.create(AuthApi.class);


    private Map<Token, String> getNewCookies() {
        log.info("Get new cookies");
        Map<Token, String> cookies;
        final Response<Void> response;
        try {
            response = authApi.getCookies().execute();
            cookies = parseCookies(response);
        } catch (IOException ex) {
            throw new AssertionError(ex);
        }
        Assertions.assertEquals(HttpStatus.FOUND, response.code());
        return cookies;
    }

    private Map<Token, String> parseCookies(Response<?> response) {
        return response.headers()
                .values("Set-Cookie").stream()
                .map(cookieText -> cookieText.split("="))
                .collect(toMap(
                        cookieArray -> Token.getEnumByCookieName(cookieArray[0]).orElse(null),
                        cookieArray -> cookieArray[1].replaceAll("; Path", "")
                ));
    }

    public Map<Token, String> register(RegisterModel registerModel) {
        var cookies = getNewCookies();

        log.info("Register user: username = [{}], password = [{}]", registerModel.getUsername(), registerModel.getPassword());
        final Response<Void> response;
        try {
            response = authApi.register(
                            registerModelToMap.toRegisterMap(registerModel.setCsrf(cookies.get(Token.CSRF))),
                            "XSRF-TOKEN=" + cookies.get(Token.CSRF),
                            "JSESSIONID=" + cookies.get(Token.JSESSIONID)
                    )
                    .execute();
        } catch (IOException ex) {
            throw new AssertionError(ex);
        }

        Assertions.assertEquals(HttpStatus.CREATED, response.code());
        return parseCookies(response);

    }

    public Map<Token, String> login(LoginModel loginModel) {

        var cookies = getNewCookies();

        log.info("Login user: username = [{}], password = [{}]", loginModel.getUsername(), loginModel.getPassword());
        final Response<Void> response;
        try {
            response = authApi.login(
                            loginModelToMap.toLoginMap(loginModel.setCsrf(cookies.get(Token.CSRF))),
                            cookies.get(Token.CSRF),
                            cookies.get(Token.JSESSIONID)
                    )
                    .execute();
        } catch (IOException ex) {
            throw new AssertionError(ex);
        }

        Assertions.assertEquals(HttpStatus.OK, response.code());
        return parseCookies(response);

    }

}