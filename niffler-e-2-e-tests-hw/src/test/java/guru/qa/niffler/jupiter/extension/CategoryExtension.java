package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.CategoryApiClient;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.CreateNewUser;
import guru.qa.niffler.mapper.CategoryMapper;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserModel;
import guru.qa.niffler.utils.CategoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
public class CategoryExtension implements BeforeEachCallback, ParameterResolver, AfterEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final CategoryApiClient categoryApiClient = new CategoryApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {

        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(CreateNewUser.class)
                        && parameter.getType().isAssignableFrom(UserModel.class))
                .forEach(
                        parameter -> {
                            var parameterName = parameter.getName();
                            var userAnno = parameter.getAnnotation(CreateNewUser.class);

                            if (userAnno.categories().length > 0) {

                                @SuppressWarnings("unchecked")
                                Map<String, UserModel> usersMap = ((Map<String, UserModel>) context
                                        .getStore(CreateNewUserExtension.NAMESPACE)
                                        .get(context.getUniqueId()));
                                var user = usersMap.get(parameterName);

                                List<CategoryJson> categories = new ArrayList<>();
                                Category categoryAnno = userAnno.categories()[0];

                                CategoryJson category = new CategoryMapper()
                                        .updateDtoFromAnno(
                                                CategoryUtils.generate().setUsername(user.getUsername()),
                                                categoryAnno
                                        );

                                category = categoryApiClient.createCategory(category.setArchived(false));
                                if (categoryAnno.isArchived())
                                    category = categoryApiClient.updateCategory(category.setArchived(true));

                                categories.add(category);

                                context.getStore(NAMESPACE).put(
                                        context.getUniqueId(),
                                        usersMap.put(parameterName, user.setCategories(categories))
                                );

                                log.info("Created new categories for user = [{}]: {}", user.getUsername(), user.getCategories());

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

                            usersMap.get(parameterName).getCategories()
                                    .forEach(category -> categoryApiClient.updateCategory(category.setArchived(true)));

                        }

                );

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }


}