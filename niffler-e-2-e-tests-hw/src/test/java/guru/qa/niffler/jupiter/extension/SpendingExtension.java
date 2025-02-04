package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.CreateNewUser;
import guru.qa.niffler.mapper.SpendMapper;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.TestData;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.api.impl.SpendApiClientImpl;
import guru.qa.niffler.utils.SpendUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@ParametersAreNonnullByDefault
public class SpendingExtension implements BeforeEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);
    private final SpendClient spendClient = new SpendApiClientImpl();

    @Override
    public void beforeEach(ExtensionContext context) {

        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(CreateNewUser.class) &&
                        parameter.getType().isAssignableFrom(UserJson.class))
                .forEach(parameter -> {

                            var parameterName = parameter.getName();
                            var userAnno = parameter.getAnnotation(CreateNewUser.class);

                            if (userAnno.spendings().length > 0) {

                                UserJson user = CreateNewUserExtension.getUserByTestParamName(parameterName);

                                List<SpendJson> spendings = new ArrayList<>();
                                Arrays.stream(userAnno.spendings())
                                        .forEach(spendAnno -> {

                                            SpendJson spend = new SpendMapper().updateFromAnno(
                                                    SpendUtils.generateForUser(user.getUsername()),
                                                    spendAnno
                                            );

                                            // Always creating new category if create new category by orElse(spend.getCategory())
                                            CategoryJson category = spendClient.findCategoryByUsernameAndName(
                                                            spend.getUsername(),
                                                            spend.getCategory().getName())
                                                    .orElse(null);
                                            if (category == null)
                                                category = spendClient.createCategory(spend.getCategory());
                                            spend.setCategory(category);

                                            spend = spendClient.create(spend);
                                            spendings.add(spend);

                                        });

                                var testData = user.getTestData().setSpendings(spendings);
                                CreateNewUserExtension.setUserByTestParamName(parameterName, user.setTestData(testData));

                                log.info("Created new spendings for user = [{}]: {}",
                                        user.getUsername(),
                                        user.getTestData().getSpendings());
                            }

                        }

                );

    }

}