package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.enums.NonStaticBrowserType;
import guru.qa.niffler.jupiter.extension.NonStaticBrowserTypeExtension;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.nonstatic.NonStaticRegisterPage;
import guru.qa.niffler.jupiter.converter.NonStaticBrowserArgumentConverter;
import guru.qa.niffler.utils.UserUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

@ExtendWith({
        NonStaticBrowserTypeExtension.class
})
class NonStaticRegisterWebTests {

    static final String REGISTRATION_PAGE_URL = Config.getInstance().authUrl() + "register";

    @ParameterizedTest
    @EnumSource(NonStaticBrowserType.class)
    void canRegisterUserWithCorrectCredentialsTest(
            @ConvertWith(NonStaticBrowserArgumentConverter.class) SelenideDriver driver
    ) {
        UserJson user1 = UserUtils.generateUser();
        driver.open(REGISTRATION_PAGE_URL);
        new NonStaticRegisterPage(driver)
                .signUpSuccess(user1)
                .shouldVisiblePageElements();
    }

}