package guru.qa.niffler.mapper;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;

import java.util.Random;

import static guru.qa.niffler.helper.StringHelper.isNotNullOrBlank;

public class CategoryMapper {

    public CategoryJson update(CategoryJson oldCategory, CategoryJson newCategory) {
        return new CategoryJson(
                newCategory.id() != null
                        ? newCategory.id()
                        : oldCategory.id(),
                isNotNullOrBlank(newCategory.name())
                        ? newCategory.name()
                        : oldCategory.name(),
                isNotNullOrBlank(newCategory.username())
                        ? newCategory.username()
                        : oldCategory.username(),
                newCategory.archived()
        );
    }

    public CategoryJson updateFromAnno(CategoryJson category, Category anno) {
        return new CategoryJson(
                category.id(),
                (!anno.name().isEmpty() || anno.notGenerateName())
                        ? anno.name()
                        : category.name(),
                category.username(),
                anno.notGenerateIsArchived()
                        ? anno.isArchived()
                        : new Random().nextBoolean()
        );
    }

}
