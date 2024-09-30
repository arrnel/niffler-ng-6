package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.CreateNewUser;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.CategoryExtension;
import guru.qa.niffler.jupiter.extension.CreateNewUserExtension;
import guru.qa.niffler.jupiter.extension.SpendingExtension;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserModel;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.auth.LoginPage;
import guru.qa.niffler.utils.SpendUtils;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Selenide.open;

@ExtendWith({
        BrowserExtension.class
        , CreateNewUserExtension.class
        , CategoryExtension.class
        , SpendingExtension.class
})
public class SpendingWebTest {

    private static final String url = Config.getInstance().authUrl() + "login";

    @Test
    @CreateNewUser
    void canCreateSpending(@NonNull UserModel user) {

        var spend = SpendUtils.generate();

        open(url, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .getHeader()
                .goToCreateSpendingPage()
                .createNewSpending(spend);
        new MainPage().assertSpendingExists(spend);

    }

    @Test
    @CreateNewUser
    @Spending
    void canEditSpending(@NonNull UserModel user, @NonNull SpendJson spend) {

        var newSpend = SpendUtils.generate();

        open(url, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .openEditSpendingPage(spend.description())
                .editSpending(newSpend)
                .openEditSpendingPage(newSpend.description())
                .assertSpendingData(newSpend);

    }

    @Test
    @CreateNewUser
    @Spending
    void canCreateNewSpendingWithExistsDescription(@NonNull UserModel user, @NonNull SpendJson spend) {
        open(url, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .getHeader()
                .goToCreateSpendingPage()
                .createNewSpending(spend)
                .openEditSpendingPage(spend.description(), 0)
                .assertSpendingData(spend);
    }

}
