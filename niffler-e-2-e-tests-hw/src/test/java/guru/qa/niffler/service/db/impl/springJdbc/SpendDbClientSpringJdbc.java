package guru.qa.niffler.service.db.impl.springJdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.springJdbc.SpendRepositorySpringJdbc;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.mapper.CategoryMapper;
import guru.qa.niffler.mapper.SpendMapper;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.service.db.SpendDbClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@ParametersAreNonnullByDefault
public class SpendDbClientSpringJdbc implements SpendDbClient {

    private static final String SPEND_JDBC_URL = Config.getInstance().spendJdbcUrl();
    private static final CategoryMapper categoryMapper = new CategoryMapper();
    private static final SpendMapper spendMapper = new SpendMapper();

    private final SpendRepository spendRepository = new SpendRepositorySpringJdbc();
    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(DataSources.dataSource(SPEND_JDBC_URL)));

    @Override
    public SpendJson create(SpendJson spendJson) {
        log.info("Creating new spend by DTO: {}", spendJson);
        return txTemplate.execute(status ->
                spendMapper.toDto(
                        spendRepository.create(
                                spendMapper.toEntity(spendJson))));
    }

    @Override
    public Optional<SpendJson> findById(UUID id) {
        log.info("Find spend by id = [{}]", id);
        return txTemplate.execute(status ->
                spendRepository.findById(id)
                        .map(spendMapper::toDto));
    }

    @Override
    public Optional<SpendJson> findFirstSpendByUsernameAndDescription(String username, String description) {
        log.info("Find first spend by username = [{}] and description = [{}]", username, description);
        return txTemplate.execute(status ->
                        spendRepository.findFirstSpendByUsernameAndDescription(username, description)
                                .map(spendMapper::toDto));
    }

    @Override
    public List<SpendJson> findAllByUsernameAndDescription(String username, String description) {
        log.info("Find all spends by username = [{}] and description = [{}]", username, description);
        return txTemplate.execute(status ->
                spendRepository.findByUsernameAndDescription(username, description).stream()
                        .map(spendMapper::toDto)
                        .toList());
    }

    @Override
    public List<SpendJson> findAllByUsername(String username) {
        log.info("Find all spends by username = [{}]", username);
        return txTemplate.execute(status ->
                spendRepository.findAllByUsername(username).stream()
                        .map(spendMapper::toDto)
                        .toList());
    }

    @Override
    public List<SpendJson> findAll() {
        log.info("Find all spends");
        return txTemplate.execute(status ->
                spendRepository.findAll().stream()
                        .map(spendMapper::toDto)
                        .toList());
    }

    @Override
    public SpendJson update(SpendJson spendJson) {
        log.info("Update spend: {}", spendJson);
        return txTemplate.execute(status ->
                spendMapper.toDto(
                        spendRepository.update(
                                spendMapper.toEntity(spendJson))));
    }

    @Override
    public void remove(SpendJson spendJson) {
        log.info("Remove spend: {}", spendJson);
        txTemplate.execute(status -> {
            spendRepository.remove(spendMapper.toEntity(spendJson));
            return null;
        });
    }

    @Override
    public CategoryJson createCategory(CategoryJson categoryJson) {
        log.info("Creating new category by DTO: {}", categoryJson);
        return txTemplate.execute(status ->
                categoryMapper.toDto(
                        spendRepository.createCategory(
                                categoryMapper.toEntity(categoryJson))));
    }

    @Override
    public Optional<CategoryJson> findCategoryById(UUID id) {
        log.info("Find category by id = [{}]", id);
        return txTemplate.execute(status ->
                spendRepository.findCategoryById(id)
                        .map(categoryMapper::toDto));
    }

    @Override
    public Optional<CategoryJson> findCategoryByUsernameAndName(String username, String name) {
        log.info("Find category by username = [{}] and name = [{}]", username, name);
        return txTemplate.execute(status ->
                spendRepository.findCategoryByUsernameAndName(username, name)
                        .map(categoryMapper::toDto));
    }

    @Override
    public List<CategoryJson> findAllCategoriesByUsername(String username) {
        log.info("Find all categories by username = [{}]", username);
        return txTemplate.execute(status ->
                spendRepository.findAllCategoriesByUsername(username).stream()
                        .map(categoryMapper::toDto)
                        .toList());
    }

    @Override
    public List<CategoryJson> findAllCategories() {
        log.info("Find all categories");
        return txTemplate.execute(status ->
                spendRepository.findAllCategories().stream()
                        .map(categoryMapper::toDto)
                        .toList());
    }

    @Override
    public CategoryJson updateCategory(CategoryJson categoryJson) {
        log.info("Update spend: {}", categoryJson);
        return txTemplate.execute(status ->
                categoryMapper.toDto(
                        spendRepository.updateCategory(
                                categoryMapper.toEntity(categoryJson))));
    }

    @Override
    public void removeCategory(CategoryJson categoryJson) {
        log.info("Remove category: {}", categoryJson);
        txTemplate.execute(status -> {
            spendRepository.removeCategory(categoryMapper.toEntity(categoryJson));
            return null;
        });
    }

}
