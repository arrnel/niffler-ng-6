package guru.qa.niffler.data.dao.impl.springJdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.rowMapper.SpendRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendDaoSpringJdbc implements SpendDao {

    private static final String SPEND_JDBC_URL = Config.getInstance().spendJdbcUrl();

    public @Nonnull SpendEntity create(SpendEntity spend) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(SPEND_JDBC_URL));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {

                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO \"spend\" (username, spend_date, currency, amount, description, category_id) " +
                                    "VALUES (?, ?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setString(1, spend.getUsername());
                    ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
                    ps.setString(3, spend.getCurrency().name());
                    ps.setDouble(4, spend.getAmount());
                    ps.setString(5, spend.getDescription());
                    ps.setObject(6, spend.getCategory().getId());
                    return ps;

                },
                keyHolder

        );

        final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        return spend.setId(generatedKey);

    }

    public @Nonnull Optional<SpendEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(SPEND_JDBC_URL));
        try {
            // QueryForObject not returns null if not found object. Method throws EmptyResultDataAccessException
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "SELECT * FROM \"spend\" WHERE id = ?",
                            SpendRowMapper.INSTANCE,
                            id
                    ));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public @Nonnull List<SpendEntity> findByUsernameAndDescription(String username, String description) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(SPEND_JDBC_URL));
        return jdbcTemplate.query(
                "SELECT * FROM \"spend\" WHERE username = ? AND description = ?",
                SpendRowMapper.INSTANCE,
                username,
                description
        );
    }

    @Override
    public @Nonnull List<SpendEntity> findAllByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(SPEND_JDBC_URL));
        return jdbcTemplate.query(
                "SELECT * FROM \"spend\" WHERE username = ?",
                SpendRowMapper.INSTANCE,
                username
        );
    }

    @Override
    public @Nonnull List<SpendEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(SPEND_JDBC_URL));
        return jdbcTemplate.query(
                "SELECT * FROM \"spend\"",
                SpendRowMapper.INSTANCE
        );
    }

    public @Nonnull SpendEntity update(SpendEntity spend) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(SPEND_JDBC_URL));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {

                    PreparedStatement ps = connection.prepareStatement(
                            "UPDATE \"spend\" SET username = ?, spend_date = ?, currency = ?, amount = ?, description = ?, category_id = ? WHERE id = ?"
                    );
                    ps.setString(1, spend.getUsername());
                    ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
                    ps.setString(3, spend.getCurrency().name());
                    ps.setDouble(4, spend.getAmount());
                    ps.setString(5, spend.getDescription());
                    ps.setObject(6, spend.getCategory().getId());
                    ps.setObject(7, spend.getId());
                    return ps;

                },
                keyHolder

        );

        return spend;

    }

    @Override
    public void remove(SpendEntity spend) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(SPEND_JDBC_URL));
        jdbcTemplate.update(
                "DELETE FROM \"spend\" WHERE id = ?",
                spend.getId()
        );
    }

}
