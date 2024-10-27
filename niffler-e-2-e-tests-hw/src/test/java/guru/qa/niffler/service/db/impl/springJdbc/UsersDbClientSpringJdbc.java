package guru.qa.niffler.service.db.impl.springJdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.jdbc.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.jdbc.UserdataUserRepositoryJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.ex.UserNotFoundException;
import guru.qa.niffler.mapper.AuthUserMapper;
import guru.qa.niffler.mapper.UserMapper;
import guru.qa.niffler.model.UserModel;
import guru.qa.niffler.service.db.UsersDbClient;
import guru.qa.niffler.utils.UserUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class UsersDbClientSpringJdbc implements UsersDbClient {

    private static final AuthUserMapper authUserMapper = new AuthUserMapper();
    private static final UserMapper userMapper = new UserMapper();
    private static final Config CFG = Config.getInstance();
    private static final String AUTH_JDBC_URL = CFG.authJdbcUrl();
    private static final String USERDATA_JDBC_URL = CFG.userdataJdbcUrl();
    private static final String SPEND_JDBC_URL = CFG.spendJdbcUrl();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc();
    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryJdbc();
    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(AUTH_JDBC_URL, USERDATA_JDBC_URL, SPEND_JDBC_URL);

    @Override
    public UserModel createUser(@NonNull UserModel userModel) {

        log.info("Creating new user with authorities in niffler-auth and niffler-userdata by DTO: {}", userModel);

        var authUserEntity = authUserMapper.toEntity(userMapper.toAuthDto(userModel));
        authUserEntity.setAuthorities(
                List.of(AuthAuthorityEntity.builder().authority(Authority.read).user(authUserEntity).build(),
                        AuthAuthorityEntity.builder().authority(Authority.write).user(authUserEntity).build())
        );

        return xaTxTemplate.execute(() -> {
            authUserRepository.create(authUserEntity);
            return userMapper.toDto(
                    userdataUserRepository.create(
                            userMapper.toEntity(userModel)));

        });

    }

    @Override
    public void getIncomeInvitationFromNewUsers(@NonNull UserModel requester, int count) {

        if (count > 0) {
            UserEntity requesterEntity = userdataUserRepository.findById(
                    requester.getId()
            ).orElseThrow(() -> new UserNotFoundException("User with id = [" + requester.getId() + "] not found"));

            for (int i = 0; i < count; i++) {
                xaTxTemplate.execute(() -> {
                            var addressee = createRandomUserIn2Dbs();
                            log.info("Create invitation from [{}] to [{}] with status", requester.getUsername(), addressee.getUsername());
                            userdataUserRepository.sendInvitation(
                                    requesterEntity,
                                    addressee,
                                    FriendshipStatus.PENDING);
                            return null;
                        }

                );
            }
        }
    }

    @Override
    public void sendOutcomeInvitationToNewUsers(@NonNull UserModel requester, int count) {

        if (count > 0) {
            UserEntity requesterEntity = userdataUserRepository.findById(
                    requester.getId()
            ).orElseThrow(() -> new UserNotFoundException("User with id = [" + requester.getId() + "] not found"));

            for (int i = 0; i < count; i++) {
                xaTxTemplate.execute(() -> {
                            var addressee = createRandomUserIn2Dbs();
                            log.info("Create invitation from [{}] to [{}] with status", requester.getUsername(), addressee.getUsername());
                            userdataUserRepository.sendInvitation(
                                    addressee,
                                    requesterEntity,
                                    FriendshipStatus.PENDING);
                            return null;
                        }
                );
            }
        }
    }

    @Override
    public void addNewFriends(@NonNull UserModel requester, int count) {

        if (count > 0) {
            UserEntity requesterEntity = userdataUserRepository.findById(
                    requester.getId()
            ).orElseThrow(() -> new UserNotFoundException("User with id = [" + requester.getId() + "] not found"));

            for (int i = 0; i < count; i++) {
                xaTxTemplate.execute(() -> {
                            var addressee = createRandomUserIn2Dbs();
                            log.info("Make users are friends: [{}], [{}]", requester.getUsername(), addressee.getUsername());
                            userdataUserRepository.addFriend(
                                    requesterEntity,
                                    addressee);
                            return null;
                        }
                );
            }
        }
    }

    @Override
    public void removeUser(@NonNull UserModel userModel) {
        log.info("Remove user from niffler-auth and niffler-userdata with username = [{}]", userModel.getUsername());
        xaTxTemplate.execute(() -> {
            authUserRepository.findByUsername(userModel.getUsername())
                    .ifPresent(authUserRepository::remove);
            userdataUserRepository.findByUsername(userModel.getUsername())
                    .ifPresent(userdataUserRepository::remove);
            return null;
        });
    }

    private UserEntity createRandomUserIn2Dbs() {

        var generatedUser = UserUtils.generateUser();
        log.info("Creating new user with authorities in niffler-auth and niffler-userdata by DTO: {}", generatedUser);

        var authUserEntity = authUserMapper.toEntity(userMapper.toAuthDto(generatedUser));
        authUserEntity.setAuthorities(
                List.of(AuthAuthorityEntity.builder().authority(Authority.read).user(authUserEntity).build(),
                        AuthAuthorityEntity.builder().authority(Authority.write).user(authUserEntity).build())
        );

        authUserRepository.create(authUserEntity);
        return userdataUserRepository.create(userMapper.toEntity(generatedUser));

    }

}
