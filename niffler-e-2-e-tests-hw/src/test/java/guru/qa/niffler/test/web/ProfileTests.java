package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.CreateNewUser;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.CategoryExtension;
import guru.qa.niffler.jupiter.extension.CreateNewUserExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserModel;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.page.auth.LoginPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.util.Objects;

@ExtendWith({
        BrowserExtension.class
        , CreateNewUserExtension.class
        , CategoryExtension.class
})
public class ProfileTests {

    static final Faker fake = new Faker();
    static final String url = Config.getInstance().authUrl() + "login";

    final ProfilePage profile = new ProfilePage();

    @Test
    @CreateNewUser
    void canAddName(UserModel user) {

        var name = fake.name().fullName();

        Selenide.open(url, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .getHeader()
                .openUserMenu()
                .goToProfilePage()
                .setName(name);

        Selenide.refresh();
        Assertions.assertEquals(name, profile.getName());

    }

    @Test
    @CreateNewUser
    void canUploadAvatar(UserModel user) {

        var file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("img/cat.jpeg")).getFile());

        Selenide.open(url, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .getHeader()
                .openUserMenu()
                .goToProfilePage()
                .uploadAvatar(file)
                .save();

        Selenide.refresh();
        profile.assertAvatarHasImage();

    }

    @Test
    @CreateNewUser
    void canCreateNewCategory(UserModel user) {

        var categoryName = fake.company().industry();

        Selenide.open(url, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .getHeader()
                .openUserMenu()
                .goToProfilePage()
                .addNewCategory(categoryName);

        Selenide.refresh();
        profile.assertCategoryExists(categoryName);

    }

    @Test
    @CreateNewUser
    @Category
    void canNotCreateCategoryWithExistsName(UserModel user, CategoryJson category) {
        Selenide.open(url, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .getHeader()
                .openUserMenu()
                .goToProfilePage()
                .addNewCategory(category.name())
                .assertAlertMessageHasText("Error while adding category %s: Cannot save duplicates".formatted(category.name()));
    }

    @Test
    @CreateNewUser
    @Category
    void canChangeCategoryName(UserModel user, CategoryJson category) {
        var newCategoryName = fake.company().industry();
        Selenide.open(url, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .getHeader()
                .openUserMenu()
                .goToProfilePage()
                .changeCategoryName(category.name(), newCategoryName)
                .assertCategoryExists(newCategoryName);
    }

    @Test
    @CreateNewUser
    @Category
    void canSetCategoryArchived(UserModel user, CategoryJson category) {
        Selenide.open(url, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .getHeader()
                .openUserMenu()
                .goToProfilePage()
                .changeCategoryStatus(category.name(), false)
                .toggleShowArchived(true)
                .assertCategoryHasStatus(category.name(), true);
    }

    @Test
    @CreateNewUser
    @Category(isArchived = true)
    void canSetCategoryUnarchivedName(UserModel user, CategoryJson category) {
        Selenide.open(url, LoginPage.class)
                .login(user.getUsername(), user.getPassword())
                .getHeader()
                .openUserMenu()
                .goToProfilePage()
                .toggleShowArchived(true)
                .changeCategoryStatus(category.name(), true)
                .assertCategoryHasStatus(category.name(), true);

    }

}
