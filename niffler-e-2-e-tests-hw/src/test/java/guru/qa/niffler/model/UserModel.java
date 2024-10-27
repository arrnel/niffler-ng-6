package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
public class UserModel {

    private UUID id;

    private String username;

    private String password;

    private String passwordConfirmation;

    private CurrencyValues currency;

    private String firstName;

    private String surname;

    private String photo;

    private String photoSmall;

    private String fullName;

    private transient TestData testData;

    public UserModel addTestData(TestData testData) {
        return UserModel.builder()
                .id(this.id)
                .username(this.username)
                .password(this.password)
                .passwordConfirmation(this.passwordConfirmation)
                .currency(this.currency)
                .firstName(this.firstName)
                .surname(this.surname)
                .photo(this.photo)
                .photoSmall(this.photoSmall)
                .fullName(this.fullName)
                .testData(testData)
                .build();
    }

}