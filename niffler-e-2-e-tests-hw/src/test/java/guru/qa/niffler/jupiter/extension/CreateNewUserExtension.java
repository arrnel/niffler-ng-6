package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.CreateNewUser;
import guru.qa.niffler.mapper.UserMapper;
import guru.qa.niffler.model.UserModel;
import guru.qa.niffler.service.jdbc.CategoryDbClient;
import guru.qa.niffler.service.jdbc.SpendDbClient;
import guru.qa.niffler.service.jdbc.UserDbClient;
import guru.qa.niffler.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CreateNewUserExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CreateNewUserExtension.class);
    private static final UserMapper userMapper = new UserMapper();

    @Override
    public void beforeEach(ExtensionContext context) {

        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(CreateNewUser.class) && parameter.getType().isAssignableFrom(UserModel.class))
                .forEach(parameter -> {

                    var parameterName = parameter.getName();
                    var parameterAnno = parameter.getAnnotation(CreateNewUser.class);
                    UserModel user = userMapper.updateFromAnno(UserUtils.generateUser(), parameterAnno);
                    new UserDbClient().createUserInAuthAndUserdataDBs(user);

                    /* sometimes user absent in userdata db if create user by api
                    authApiClient.register(registerModelMapper.fromUserModel(user));
                    user.setId(userdataDbClient.findByUsername(user.getUsername()).get().getId());
                    */

                    @SuppressWarnings("unchecked")
                    Map<String, UserModel> usersMap = ((Map<String, UserModel>) context.getStore(NAMESPACE)
                            .getOrComputeIfAbsent(context.getUniqueId(), map -> new HashMap<>()));
                    usersMap.put(parameterName, user);

                    log.info("Created new user: {}", user);

                });

    }

    @Override
    public void afterEach(ExtensionContext context) {

        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(CreateNewUser.class)
                        && parameter.getType().isAssignableFrom(UserModel.class))
                .forEach(
                        parameter -> {
                            var parameterName = parameter.getName();

                            @SuppressWarnings("unchecked")
                            Map<String, UserModel> usersMap = (Map<String, UserModel>) context
                                    .getStore(CreateNewUserExtension.NAMESPACE)
                                    .get(context.getUniqueId());

                            UserModel user = usersMap.get(parameterName);

                            var spendDbClient = new SpendDbClient();
                            spendDbClient.findAllByUsername(user.getUsername()).forEach(spendDbClient::delete);

                            var categoryDbClient = new CategoryDbClient();
                            categoryDbClient.findAllByUsername(user.getUsername()).forEach(categoryDbClient::delete);

                            new UserDbClient().deleteUserFromAuthAndUserdataDBs(user);

                        }
                );

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserModel.class);
    }

    @Override
    public UserModel resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        @SuppressWarnings("unchecked")
        Map<String, UserModel> usersMap = (Map<String, UserModel>) extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), Map.class);
        return usersMap.get(parameterContext.getParameter().getName());
    }

}