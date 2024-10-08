package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.CreateNewUser;
import guru.qa.niffler.jupiter.annotation.Spending;
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
        CreateNewUserExtension.class,
        CategoryExtension.class,
        SpendingExtension.class
})
class SpendingWebTests {

    static final String LOGIN_PAGE_URL = Config.getInstance().authUrl() + "login";
    final MainPage mainPage = new MainPage();

    @Test
    @CreateNewUser
    void canCreateSpendingTest(@NonNull UserModel user) {

        var spend = SpendUtils.generate();

        open(LOGIN_PAGE_URL, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .createNewSpending(spend)
                .shouldHaveSpend(spend);

    }

    @Test
    @CreateNewUser
    @Spending
    void canEditSpendingTest(@NonNull UserModel user, @NonNull SpendJson spend) {

        var newSpend = SpendUtils.generate();

        open(LOGIN_PAGE_URL, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .openEditSpendingPage(spend)
                .editSpending(newSpend)
                .openEditSpendingPage(newSpend)
                .shouldHaveData(newSpend);

    }

    @Test
    @CreateNewUser
    @Spending
    void canCreateNewSpendingWithExistsDescriptionTest(@NonNull UserModel user, @NonNull SpendJson spend) {
        open(LOGIN_PAGE_URL, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .createNewSpending(spend)
                .shouldVisiblePageElements()
                .shouldHaveSpends(spend, 2);
    }

}