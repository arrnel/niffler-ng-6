package guru.qa.niffler.mapper;

import guru.qa.niffler.model.RegisterModel;
import guru.qa.niffler.model.UserModel;

import java.util.Map;

public class RegisterModelMapper {

    public Map<String, String> toRegisterMap(RegisterModel source) {
        return Map.of(
                "_csrf", source.getCsrf(),
                "username", source.getUsername(),
                "password", source.getPassword(),
                "passwordSubmit", source.getPasswordConfirmation()
        );
    }

    public RegisterModel fromUserModel(UserModel source) {
        return RegisterModel.builder()
                .username(source.getUsername())
                .password(source.getPassword())
                .passwordConfirmation(source.getPasswordConfirmation())
                .build();
    }

}