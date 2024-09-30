package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.CreateNewUser;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.CreateNewUserExtension;
import guru.qa.niffler.model.UserModel;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.auth.LoginPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Slf4j
@ExtendWith({
        CreateNewUserExtension.class,
        BrowserExtension.class
})
public class LoginTests {

    private static final String url = Config.getInstance().authUrl() + "login";

    @Test
    @CreateNewUser
    void shouldLoginWithCorrectCredentialsTest(UserModel user) {
        Selenide.open(url, LoginPage.class)
                .login(user.getUsername(), user.getPassword());
        new MainPage().assertMainPageAndElementsAreVisible();
    }

    @Test
    @CreateNewUser
    void shouldDisplayErrorMessageIfPasswordIsIncorrectTest(UserModel user) {
        Selenide.open(url, LoginPage.class)
                .login(user.getUsername(), new Faker().internet().password());
        new LoginPage().assertBadCredentialsErrorVisible();
    }

    @Test
    @CreateNewUser
    void shouldDisplayErrorMessageIfUsernameNotFoundTest() {
        Faker faker = new Faker();
        Selenide.open(url, LoginPage.class)
                .login(faker.name().username() + faker.number().randomNumber(), faker.internet().password());
        new LoginPage().assertBadCredentialsErrorVisible();
    }

    @Test
    @CreateNewUser
    void canSignOutTest(UserModel user) {
        Selenide.open(url, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .getHeader()
                .openUserMenu()
                .signOut()
                .assertPage();
    }

}
