package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.CategoryApiClient;
import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.mapper.SpendMapper;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserModel;
import guru.qa.niffler.utils.CategoryUtils;
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
    private SpendJson spend;
    private boolean isCategoryWasCreatedBySpend = true;
    private boolean categoryWasArchivedBefore = false;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Spending.class)
                .ifPresent(anno -> {

                    UserModel user = Optional.ofNullable(context.getStore(CreateNewUserExtension.NAMESPACE)
                            .get(context.getUniqueId(), UserModel.class)).orElse(new UserModel());

                    log.info("SpendingExtension got user: {}", user);

                    spend = spendApiClient.createNewSpend(getSpend(user, anno));
                    context.getStore(NAMESPACE).put(context.getUniqueId(), spend);

                    log.info("Created spending: {}", spend);

                });

    }

    public SpendJson getSpend(@NonNull UserModel user, Spending anno) {

        var username = getUsernameOrThrowException(user, anno.username());
        var spend = SpendUtils.generate();
        var category = spend.category();
        spend = spend
                .category(category
                        .username(username)
                        .name(getCategoryName(username, anno)))
                .username(username);

        return new SpendMapper().updateFromAnno(spend, anno);
    }

    public String getUsernameOrThrowException(@NonNull UserModel user, @NonNull String annoUsername) {
        var username = user.getUsername();
        if (isNullOrEmpty(username) && isNullOrEmpty(annoUsername)) {
            throw new IllegalArgumentException("Username should contains in @Spending or should add @CreateNewUser on test");
        } else if (isNotNullOrEmpty(username) && isNotNullOrEmpty(annoUsername) && !username.equals(annoUsername)) {
            throw new IllegalArgumentException("You can not set different usernames in @Spending(username = [%s]) and @CreateNewUser(username = [%s])"
                    .formatted(annoUsername, username));
        } else {
            return username;
        }
    }


    private String getCategoryName(String username, Spending anno) {

        List<CategoryJson> existsCategories = categoryApiClient.getAllCategories(username, false);
        var categoryName = (!anno.category().isEmpty() || anno.notGenerateCategory())
                ? anno.category()
                : CategoryUtils.generate().name();
        CategoryJson category = getCategoryFromList(existsCategories, categoryName);
        if (category != null) {
            isCategoryWasCreatedBySpend = false;
            categoryWasArchivedBefore = category.archived();
        }
        return categoryName;
    }

    private CategoryJson getCategoryFromList(List<CategoryJson> categories, String name) {
        return categories.stream().filter(categoryJson -> categoryJson.name().equals(name)).findFirst().orElse(null);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
    }

    @Override
    public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), SpendJson.class);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {

        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Spending.class)
                .ifPresent(anno -> {

                    var spend = context.getStore(NAMESPACE).get(context.getUniqueId(), SpendJson.class);

                    spendApiClient.deleteSpends(spend.username(), List.of(spend.id().toString()));
                    if (isCategoryWasCreatedBySpend && categoryWasArchivedBefore)
                        categoryApiClient.updateCategory(spend.category().archived(false));

                });

    }
}
