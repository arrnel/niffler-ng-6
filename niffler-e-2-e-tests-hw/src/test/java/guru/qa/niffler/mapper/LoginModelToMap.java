package guru.qa.niffler.mapper;

import guru.qa.niffler.model.LoginModel;
import guru.qa.niffler.model.UserModel;
import lombok.NonNull;

import java.util.Map;

public class LoginModelToMap {

    public Map<String, String> toLoginMap(@NonNull LoginModel source) {
        return Map.of(
                "_csrf", source.getCsrf(),
                "username", source.getUsername(),
                "password", source.getPassword()
        );
    }

    public LoginModel fromUserModel(@NonNull UserModel source) {
        return LoginModel.builder()
                .username(source.getUsername())
                .password(source.getPassword())
                .build();
    }

}