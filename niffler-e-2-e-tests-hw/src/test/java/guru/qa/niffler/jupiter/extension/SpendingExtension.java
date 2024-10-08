package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.CategoryApiClient;
import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.mapper.SpendMapper;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserModel;
import guru.qa.niffler.utils.SpendUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.List;
import java.util.Optional;

import static guru.qa.niffler.helper.StringHelper.isNotNullOrEmpty;
import static guru.qa.niffler.helper.StringHelper.isNullOrEmpty;

@Slf4j
public class SpendingExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

    private final SpendApiClient spendApiClient = new SpendApiClient();
    private final CategoryApiClient categoryApiClient = new CategoryApiClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Spending.class)
                .ifPresent(anno -> {

                    UserModel user = Optional.ofNullable(context.getStore(CreateNewUserExtension.NAMESPACE)
                            .get(context.getUniqueId(), UserModel.class)).orElse(new UserModel());
                    if (user.getUsername() != null) log.info("User from @CreateNewUser: {}", user);

                    checkUsernameIsCorrectlyFilledInSpendingAndCreateNewUserAnnotations(user.getUsername(), anno.username());
                    var username = anno.username().isEmpty()
                            ? user.getUsername()
                            : anno.username();

                    var spend = generateSpendForUserAndLazyUpdateBySpendingAnnotation(username, anno);
                    context.getStore(NAMESPACE).put(context.getUniqueId(), spendApiClient.createNewSpend(spend));

                    log.info("Created new spend: {}", spend);

                });

    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {

        context.getStore(NAMESPACE).remove(context.getUniqueId());

        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Spending.class)
                .ifPresent(anno -> {

                    var spend = context.getStore(NAMESPACE).get(context.getUniqueId(), SpendJson.class);
                    log.info("Text spend: {}", spend);
                    log.info("Delete spend: id = [{}], description = [{}]", spend.getId().toString(), spend.getDescription());
                    spendApiClient.deleteSpends(spend.getUsername(), List.of(spend.getId().toString()));

                    // If category was created by @Spending (not by @Category), then set category archived
                    Optional.ofNullable(context.getStore(CategoryExtension.NAMESPACE)
                            .get(context.getUniqueId(), CategoryJson.class))
                            .ifPresent(
                                    category -> {
                                        if (category.getName().equals(spend.getCategory().getName())){
                                            log.info("Set category archived: name = [{}]", category.getName());
                                            categoryApiClient.updateCategory(category.setArchived(false));
                                        }
                                    }
                            );

                });

    }

    public SpendJson generateSpendForUserAndLazyUpdateBySpendingAnnotation(@NonNull String username, Spending anno) {

        var spend = SpendUtils.generate();

        spend = spend
                .setUsername(username)
                .setCategory(
                        spend.getCategory()
                                .setUsername(username));
        log.info("EDITED SPEND: {}", spend);
        return new SpendMapper().updateFromAnno(spend, anno);
    }

    public void checkUsernameIsCorrectlyFilledInSpendingAndCreateNewUserAnnotations(String username, String annoUsername) {

        if (isNullOrEmpty(username) && isNullOrEmpty(annoUsername)) {
            throw new IllegalArgumentException("Username should contains in @Spending or should add @CreateNewUser on test");
        } else if (isNotNullOrEmpty(username) && isNotNullOrEmpty(annoUsername) && !username.equals(annoUsername)) {
            throw new IllegalArgumentException("You can not set different usernames in @Spending(username = [%s]) and @CreateNewUser(username = [%s])"
                    .formatted(annoUsername, username));
        }

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
    }

    @Override
    public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), SpendJson.class);
    }

}