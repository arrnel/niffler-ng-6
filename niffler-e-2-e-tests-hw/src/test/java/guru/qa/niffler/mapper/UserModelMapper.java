package guru.qa.niffler.mapper;

import guru.qa.niffler.jupiter.annotation.CreateNewUser;
import guru.qa.niffler.model.UserModel;

public class UserModelMapper {

    public UserModel updateFromAnno(UserModel user, CreateNewUser anno) {
        return UserModel.builder()
                .username(
                        !anno.username().isEmpty()
                                ? anno.username()
                                : user.getUsername())
                .password(
                        !anno.password().isEmpty()
                                ? anno.password()
                                : user.getPassword())
                .passwordConfirmation(
                        !anno.password().isEmpty()
                                ? anno.password()
                                : user.getPasswordConfirmation())
                .fullName(
                        !anno.fullName().isEmpty()
                                ? anno.fullName()
                                : user.getFullName())
                .avatar(
                        !anno.avatar().isEmpty()
                                ? anno.avatar()
                                : user.getAvatar())
                .build();

    }

}
