package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.CategoryApiClient;
import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.helper.CollectionsHelper;
import guru.qa.niffler.jupiter.annotation.CreateNewUser;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.mapper.SpendMapper;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserModel;
import guru.qa.niffler.utils.SpendUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static guru.qa.niffler.helper.StringHelper.isNotNullOrEmpty;
import static guru.qa.niffler.helper.StringHelper.isNullOrEmpty;

@Slf4j
public class SpendingExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

    private final SpendApiClient spendApiClient = new SpendApiClient();
    private final CategoryApiClient categoryApiClient = new CategoryApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {

        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(CreateNewUser.class) && parameter.getType().isAssignableFrom(UserModel.class))
                .forEach(
                        parameter -> {
                            var parameterName = parameter.getName();
                            var userAnno = parameter.getAnnotation(CreateNewUser.class);
                            if (userAnno.spendings().length > 0) {

                                @SuppressWarnings("unchecked")
                                Map<String, UserModel> usersMap = (Map<String, UserModel>) context.getStore(CreateNewUserExtension.NAMESPACE)
                                        .get(context.getUniqueId());
                                UserModel user = usersMap.get(parameterName);

                                List<SpendJson> spendings = new ArrayList<>();
                                Arrays.stream(userAnno.spendings()).forEach(spendAnno -> {
                                    SpendJson spend = new SpendMapper()
                                            .updateFromAnno(
                                                    SpendUtils.generate().setUsername(user.getUsername()),
                                                    spendAnno
                                            );
                                    spend.getCategory().setUsername(user.getUsername());
                                    spendings.add(spendApiClient.createNewSpend(spend));
                                });

                                context.getStore(NAMESPACE).put(
                                        context.getUniqueId(),
                                        usersMap.put(parameterName, user.setSpendings(spendings))
                                );

                                log.info("Created new spendings for user = [{}]: {}", user.getUsername(), user.getSpendings());
                            }

                        }

                );

    }

    @Override
    public void afterEach(ExtensionContext context) {

        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(CreateNewUser.class) && parameter.getType().isAssignableFrom(UserModel.class))
                .forEach(
                        parameter -> {
                            var parameterName = parameter.getName();

                            @SuppressWarnings("unchecked")
                            Map<String, UserModel> usersMap = (Map<String, UserModel>) context.getStore(CreateNewUserExtension.NAMESPACE)
                                    .get(context.getUniqueId());

                            UserModel user = usersMap.get(parameterName);

                            List<SpendJson> spendings = user.getSpendings();
                            List<CategoryJson> categories = user.getCategories();

                            if (CollectionsHelper.isNotNullOrEmpty(spendings)) {
                                spendApiClient.deleteSpends(user.getUsername(), spendings.stream().map(spend -> spend.getId().toString()).toList());
                                spendings.stream().map(SpendJson::getCategory)
                                        .filter(spendCategory -> categories.stream().noneMatch(category -> category.getName().equals(spendCategory.getName())))
                                        .forEach(category -> categoryApiClient.updateCategory(category.setArchived(true)));
                            }


                        }

                );

    }

    public SpendJson generateAndUpdateBySpendingAnno(@NonNull String username, Spending anno) {

        var spend = SpendUtils.generate();

        spend = spend
                .setUsername(username)
                .setCategory(
                        spend.getCategory()
                                .setUsername(username));
        log.info("EDITED SPEND: {}", spend);
        return new SpendMapper().updateFromAnno(spend, anno);
    }

    public void checkUsernameIsCorrectInStoreAndSpendingAnno(String username, String annoUsername) {

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