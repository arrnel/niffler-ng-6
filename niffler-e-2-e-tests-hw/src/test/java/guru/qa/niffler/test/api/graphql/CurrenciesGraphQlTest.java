package guru.qa.niffler.test.api.graphql;

import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.CurrenciesQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.CreateNewUser;
import guru.qa.niffler.model.rest.CurrencyValues;
import guru.qa.niffler.model.rest.UserJson;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrenciesGraphQlTest extends BaseGraphqlTest {

    @Test
    void allCurrenciesShouldBeReturnFromGatewayTest(
            @ApiLogin
            @CreateNewUser()
            UserJson user
    ) {

        // Data
        final var token = user.getTestData().getToken();
        final var currenciesCall = apolloClient.query(new CurrenciesQuery())
                .addHttpHeader("Authorization", "Bearer " + token);

        // Steps
        final var response = Rx2Apollo.single(currenciesCall)
                .blockingGet();// дожидаемся асинхронного выполнения запроса и блокировкой потока пока не получили ответ

        // Assertions precondition
        final var data = response.dataOrThrow();
        final var graphQlCurrencies = data.currencies.stream()
                .map(c -> c.currency.rawValue)
                .toList();
        final var currencyValues = Arrays.stream(CurrencyValues.values())
                .map(CurrencyValues::name)
                .toList();

        // Assertions
        assertTrue(currencyValues.containsAll(graphQlCurrencies));

    }

}
