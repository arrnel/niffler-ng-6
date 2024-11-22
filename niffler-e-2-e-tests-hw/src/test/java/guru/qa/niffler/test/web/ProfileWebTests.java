package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.CreateNewUser;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.page.ProfilePage;
import guru.qa.niffler.page.page.auth.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
class ProfileWebTests {

    static final Faker FAKE = new Faker();
    static final String LOGIN_PAGE_URL = Config.getInstance().authUrl() + "login";
    final ProfilePage profile = new ProfilePage();

    @Test
    void canAddNameTest(@CreateNewUser UserJson user) {

        var name = FAKE.name().fullName();

        Selenide.open(LOGIN_PAGE_URL, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .getHeader()
                .goToProfilePage()
                .setName(name);

        Selenide.refresh();
        profile.shouldHaveName(name);

    }

    @Test
    void canUploadAvatarTest(@CreateNewUser UserJson user) {

        Selenide.open(LOGIN_PAGE_URL, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .getHeader()
                .goToProfilePage()
                .uploadAvatar("img/cat.jpeg")
                .save();

        Selenide.refresh();
        profile.shouldHaveImage();

    }

    @Test
    void canCreateNewCategoryTest(@CreateNewUser UserJson user) {

        var categoryName = FAKE.company().industry();

        Selenide.open(LOGIN_PAGE_URL, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .getHeader()
                .goToProfilePage()
                .addNewCategory(categoryName);

        Selenide.refresh();
        profile.shouldBeActiveCategory(categoryName);

    }

    @Test
    void canNotCreateCategoryWithExistsNameTest(@CreateNewUser(categories = @Category) UserJson user) {

        var categoryName = user.getTestData().getCategories().getFirst().getName();

        Selenide.open(LOGIN_PAGE_URL, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .getHeader()
                .goToProfilePage()
                .addNewCategory(categoryName)
                .shouldBeErrorAlert()
                .shouldHaveMessageAlert("Error while adding category " + categoryName + ": Cannot save duplicates");

    }

    @Test
    void canEditCategoryNameTest(@CreateNewUser(categories = @Category) UserJson user) {

        var newCategoryName = FAKE.company().industry();

        Selenide.open(LOGIN_PAGE_URL, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .getHeader()
                .goToProfilePage()
                .editCategoryName(user.getTestData().getCategories().getFirst().getName(), newCategoryName);

        Selenide.refresh();
        profile.shouldCategoryExists(newCategoryName);

    }

    @Test
    void canSetCategoryArchivedTest(@CreateNewUser(categories = @Category) UserJson user) {

        var categoryName = user.getTestData().getCategories().getFirst().getName();

        Selenide.open(LOGIN_PAGE_URL, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .getHeader()
                .goToProfilePage()
                .setCategoryArchive(categoryName);

        Selenide.refresh();
        profile.showArchivedCategories()
                .shouldBeArchiveCategory(categoryName);

    }

    @Test
    void canSetCategoryUnarchivedTest(@CreateNewUser(categories = @Category(isArchived = true)) UserJson user) {

        var categoryName = user.getTestData().getCategories().getFirst().getName();

        Selenide.open(LOGIN_PAGE_URL, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .getHeader()
                .goToProfilePage()
                .showArchivedCategories()
                .setCategoryActive(categoryName);

        Selenide.refresh();
        profile.shouldBeActiveCategory(categoryName);

    }

}