package guru.qa.niffler.mapper;

import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.jupiter.annotation.CreateNewUser;
import guru.qa.niffler.model.rest.AuthUserJson;
import guru.qa.niffler.model.rest.CurrencyValues;
import guru.qa.niffler.model.rest.UserJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.StandardCharsets;

@ParametersAreNonnullByDefault
public class UserMapper {

    public @Nonnull UserEntity toEntity(UserJson userJson) {
        return UserEntity.builder()
                .id(userJson.getId())
                .username(userJson.getUsername())
                .currency(userJson.getCurrency())
                .firstName(userJson.getFirstName())
                .surname(userJson.getSurname())
                .photo(userJson.getPhoto() != null
                        ? userJson.getPhoto().getBytes(StandardCharsets.UTF_8)
                        : null
                )
                .photoSmall(userJson.getPhotoSmall() != null
                        ? userJson.getPhotoSmall().getBytes(StandardCharsets.UTF_8)
                        : null)
                .fullName(userJson.getFullName())
                .build();
    }

    public @Nonnull AuthUserJson toAuthDto(UserJson userJson) {
        return AuthUserJson.builder()
                .id(userJson.getId())
                .username(userJson.getUsername())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .password(userJson.getPassword())
                .build();
    }

    public @Nonnull UserJson toDto(UserEntity userEntity) {
        return UserJson.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .currency(userEntity.getCurrency())
                .firstName(userEntity.getFirstName())
                .surname(userEntity.getSurname())
                .photo(
                        userEntity.getPhoto() != null && userEntity.getPhoto().length > 0
                                ? new String(userEntity.getPhoto(), StandardCharsets.UTF_8)
                                : null)
                .photoSmall(
                        userEntity.getPhotoSmall() != null && userEntity.getPhotoSmall().length > 0
                                ? new String(userEntity.getPhotoSmall(), StandardCharsets.UTF_8)
                                : null)
                .fullName(userEntity.getFullName())
                .build();
    }

    public UserJson updateFromAnno(UserJson user, CreateNewUser anno) {
        return UserJson.builder()
                .id(user.getId())
                .username(
                        anno.username().isEmpty()
                                ? user.getUsername()
                                : anno.username())
                .currency(
                        (anno.currency() != CurrencyValues.USD || anno.notGenerateCurrency())
                                ? anno.currency()
                                : user.getCurrency())
                .password(
                        anno.password().isEmpty()
                                ? user.getPassword()
                                : anno.password())
                .passwordConfirmation(
                        anno.password().isEmpty()
                                ? user.getPasswordConfirmation()
                                : anno.password())
                .firstName(
                        anno.firstName().isEmpty()
                                ? user.getFirstName()
                                : anno.firstName())
                .surname(
                        anno.surname().isEmpty()
                                ? user.getSurname()
                                : anno.surname())
                .photo(user.getPhoto())
                .photoSmall(user.getPhotoSmall())
                .fullName(
                        anno.fullName().isEmpty()
                                ? user.getFullName()
                                : anno.fullName())
                .build();
    }

}