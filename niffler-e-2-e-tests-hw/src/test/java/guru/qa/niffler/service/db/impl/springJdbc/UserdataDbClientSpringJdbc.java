package guru.qa.niffler.service.db.impl.springJdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.springJdbc.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.mapper.UserMapper;
import guru.qa.niffler.model.UserModel;
import guru.qa.niffler.service.db.UserdataDbClient;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class UserdataDbClientSpringJdbc implements UserdataDbClient {

    private static final String USERDATA_JDBC_URL = Config.getInstance().userdataJdbcUrl();
    private static final UserMapper userMapper = new UserMapper();

    private final UserdataUserDao userdataUserDao = new UserdataUserDaoSpringJdbc();
    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(DataSources.dataSource(USERDATA_JDBC_URL))
    );


    @Override
    public UserModel create(@NonNull UserModel userModel) {
        log.info("Creating new user by DTO: {}", userModel);
        return txTemplate.execute(status ->
                userMapper.toDto(
                        userdataUserDao.create(
                                userMapper.toEntity(userModel))));
    }

    @Override
    public Optional<UserModel> findById(@NonNull UUID id) {
        log.info("Get user by id = [{}]", id);
        return txTemplate.execute(status ->
                userdataUserDao
                        .findById(id)
                        .map(userMapper::toDto));
    }

    @Override
    public Optional<UserModel> findByUsername(@NonNull String username) {
        log.info("Get user by username = [{}]", username);
        return txTemplate.execute(status ->
                userdataUserDao
                        .findByUsername(username)
                        .map(userMapper::toDto));
    }

    @Override
    public List<UserModel> findAll() {
        log.info("Get all users");
        return txTemplate.execute(status ->
                userdataUserDao
                        .findAll().stream()
                        .map(userMapper::toDto)
                        .toList());
    }

    @Override
    public void sendInvitation(@NonNull UserModel requester, @NonNull UserModel addressee) {
        log.info("Send invitation from = [{}] to = [{}]", requester.getUsername());
        txTemplate.execute(status -> {
            userdataUserDao.sendInvitation(
                    userMapper.toEntity(requester),
                    userMapper.toEntity(addressee),
                    FriendshipStatus.PENDING);
            return null;
        });
    }

    @Override
    public void addFriend(@NonNull UserModel requester, @NonNull UserModel addressee) {
        log.info("Make friends [{}] and [{}]", requester.getUsername(), addressee.getUsername());
        txTemplate.execute(status -> {
            userdataUserDao.addFriend(
                    userMapper.toEntity(requester),
                    userMapper.toEntity(addressee));
            return null;
        });
    }

    @Override
    public void remove(@NonNull UserModel userModel) {
        log.info("Remove user: {}", userModel);
        txTemplate.execute(status -> {
            new UserdataUserDaoSpringJdbc()
                    .remove(userMapper.toEntity(userModel));
            return null;
        });
    }

}
