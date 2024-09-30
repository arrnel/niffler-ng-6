package guru.qa.niffler.mapper;

import guru.qa.niffler.ex.InvalidDateException;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.utils.DateHelper;

import static guru.qa.niffler.helper.StringHelper.isNotNullOrEmpty;

public class SpendMapper {

    public SpendJson updateFromAnno(SpendJson spend, Spending anno) {
        var datePattern = "MM/dd/yyyy";
        return new SpendJson(
                spend.id(),
                !anno.date().isEmpty()
                        ? DateHelper.parseDateByPattern(anno.date(), datePattern)
                        .orElseThrow(() -> new InvalidDateException("Can not parse date from text [%s] by pattern [%s]"
                                .formatted(anno.date(), datePattern)))
                        : spend.spendDate(),
                new CategoryJson(
                        null,
                        (!anno.category().isEmpty() || anno.notGenerateCategory())
                                ? anno.category()
                                : spend.category().name(),
                        !anno.username().isEmpty()
                                ? anno.username()
                                : spend.username(),
                        false),
                (anno.currency() != CurrencyValues.USD || anno.notGenerateCurrency())
                        ? anno.currency()
                        : spend.currency(),
                (anno.amount() != 0.0 || anno.notGenerateAmount())
                        ? anno.amount()
                        : spend.amount(),
                (isNotNullOrEmpty(anno.description()) || anno.notGenerateDescription())
                        ? anno.description()
                        : spend.description(),
                !anno.username().isEmpty()
                        ? anno.username()
                        : spend.username()
        );
    }

}
