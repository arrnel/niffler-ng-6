package guru.qa.niffler.test.api.graphql;

import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.CreateNewUser;
import guru.qa.niffler.model.rest.UserJson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsGraphQlTest extends BaseGraphqlTest {

    @Test
    void allCurrenciesShouldBeReturnFromGatewayTest(
            @ApiLogin
            @CreateNewUser()
            UserJson user
    ) {

        // Data
        final var token = user.getTestData().getToken();
        final var currenciesCall = apolloClient
                .query(StatQuery
                        .builder()
                        .filterCurrency(null)
                        .filterPeriod(null)
                        .statCurrency(null)
                        .build())
                .addHttpHeader("Authorization", "Bearer " + token);

        // Steps
        final var response = Rx2Apollo.single(currenciesCall)
                .blockingGet();// дожидаемся асинхронного выполнения запроса и блокировкой потока пока не получили ответ

        // Assertions precondition
        final var data = response.dataOrThrow();
        final var graphQlStat = data.stat;
        final var total = graphQlStat.total;

        // Assertions
        assertEquals(0.0, total);

    }

}
