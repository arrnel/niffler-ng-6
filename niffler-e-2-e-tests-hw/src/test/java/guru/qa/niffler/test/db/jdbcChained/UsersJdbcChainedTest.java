package guru.qa.niffler.test.db.jdbcChained;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.jdbc.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.mapper.AuthUserMapper;
import guru.qa.niffler.mapper.UserMapper;
import guru.qa.niffler.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class UsersJdbcChainedTest {

    private static final Config CFG = Config.getInstance();

    private final AuthAuthorityDao authorityDao = new AuthAuthorityDaoJdbc();
    private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
    private final UserdataUserDao userdataUserDao = new UserdataUserDaoJdbc();
    private final UserMapper userMapper = new UserMapper();
    private final AuthUserMapper authUserMapper = new AuthUserMapper();

    private final TransactionTemplate chainedTxTemplate = new TransactionTemplate(
            new ChainedTransactionManager(
                    new JdbcTransactionManager(DataSources.dataSource(CFG.authJdbcUrl())),
                    new JdbcTransactionManager(DataSources.dataSource(CFG.userdataJdbcUrl()))
            )
    );

    @Test
    void shouldCreateNewUserWithCorrectData() {

        var generatedUser = UserUtils.generateUser();

        var createdAuthUser = chainedTxTemplate.execute(status -> {

            var authUser = authUserDao.create(
                    authUserMapper.toEntity(
                            userMapper.toAuthDto(generatedUser)));

            authorityDao.create(
                    Arrays.stream(Authority.values())
                            .map(authority ->
                                    AuthAuthorityEntity.builder()
                                            .userId(authUser.getId())
                                            .authority(authority)
                                            .build())
                            .toArray(AuthAuthorityEntity[]::new));

            userdataUserDao.create(userMapper.toEntity(generatedUser));

            return authUser;

        });

        var authUser = authUserDao.findByUsername(createdAuthUser.getUsername());
        var authorities = authorityDao.findByUserId(authUser.get().getId());
        var userdataUser = userdataUserDao.findByUsername(createdAuthUser.getUsername());

        assertAll("User should exists in niffler-auth and niffler-userdata", () -> {
            assertTrue(authUser.isPresent());
            assertEquals(2, authorities.size());
            assertTrue(userdataUser.isPresent());
        });

        log.info("Auth user: {}", createdAuthUser);

    }

    @Test
    void shouldRollbackTransactionIfAuthorityUserIdIsNull() {

        var generatedUser = UserUtils.generateUser();

        chainedTxTemplate.execute(status -> {

            var authUser = authUserDao.create(
                    authUserMapper.toEntity(
                            userMapper.toAuthDto(generatedUser)));

            log.info("Auth user: {}", authUser);

            authorityDao.create(
                    Arrays.stream(Authority.values())
                            .map(authority ->
                                    AuthAuthorityEntity.builder()
                                            .userId(null)
                                            .authority(authority)
                                            .build())
                            .toArray(AuthAuthorityEntity[]::new));

            userdataUserDao.create(userMapper.toEntity(generatedUser));
            return authUser;

        });

    }


}
