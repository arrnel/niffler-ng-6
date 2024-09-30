package guru.qa.niffler.test.web;

import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserModel;
import guru.qa.niffler.page.auth.RegisterPage;
import guru.qa.niffler.utils.UserUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Selenide.open;

@ExtendWith({
        BrowserExtension.class
})
public class RegisterTests {

    private static final String url = Config.getInstance().authUrl() + "register";

    @Test
    void canRegisterUserWithCorrectCredentials() {
        UserModel user = UserUtils.generateValidUser();
        open(url, RegisterPage.class)
                .signUp(user)
                .assertSuccessfulRegistration();
    }

    @Test
    void canNotRegisterIfUsernameIsExist() {
        UserModel user = UserUtils.generateValidUser();
        open(url, RegisterPage.class)
                .signUp(user)
                .goToLoginPage()
                .goToRegisterPage()
                .signUp(user);

        new RegisterPage().assertUsernameHasError("Username `%s` already exists".formatted(user.getUsername()));
    }

    @Test
    void canNotRegisterIfPasswordAndPasswordConfirmationNotEqual() {
        UserModel user = UserUtils.generateValidUser();
        open(url, RegisterPage.class)
                .signUp(user.setPassword(new Faker().internet().password()));
        new RegisterPage().assertPasswordHasError("Passwords should be equal");
    }

    @Test
    void canNotRegisterIfUsernameIsTooShort() {
        UserModel user = UserUtils.generateValidUser();
        user.setUsername(new Faker().lorem().characters(2));
        open(url, RegisterPage.class)
                .signUp(user);
        new RegisterPage().assertUsernameHasError("Allowed username length should be from 3 to 50 characters");
    }

    @Test
    void canNotRegisterIfUsernameIsTooLong() {
        UserModel user = UserUtils.generateValidUser();
        user.setUsername(new Faker().lorem().characters(51));
        open(url, RegisterPage.class)
                .signUp(user);
        new RegisterPage().assertUsernameHasError("Allowed username length should be from 3 to 50 characters");
    }

    @Test
    void canNotRegisterIfPasswordIsTooShort() {

        UserModel user = UserUtils.generateValidUser();
        var password = new Faker().lorem().characters(2);
        user.setPassword(password).setPasswordConfirmation(password);

        open(url, RegisterPage.class)
                .signUp(user);

        new RegisterPage()
                .assertPasswordHasError("Allowed password length should be from 3 to 12 characters")
                .assertPasswordConfirmationHasError("Allowed password length should be from 3 to 12 characters");
    }

    @Test
    void canNotRegisterIfPasswordIsTooLong() {

        UserModel user = UserUtils.generateValidUser();
        var password = new Faker().lorem().characters(13);
        user.setPassword(password).setPasswordConfirmation(password);

        open(url, RegisterPage.class)
                .signUp(user);

        new RegisterPage()
                .assertPasswordHasError("Allowed password length should be from 3 to 12 characters")
                .assertPasswordConfirmationHasError("Allowed password length should be from 3 to 12 characters");
    }

}
