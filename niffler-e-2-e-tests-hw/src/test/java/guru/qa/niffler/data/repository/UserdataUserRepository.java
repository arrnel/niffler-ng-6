package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserdataUserRepository {

    UserEntity create(UserEntity user);

    Optional<UserEntity> findById(UUID id);

    Optional<UserEntity> findByUsername(String username);

    List<UserEntity> findAll();

    void sendInvitation(UserEntity requester, UserEntity addressee, FriendshipStatus status);

    void addFriend(UserEntity requester, UserEntity addressee);

    void remove(UserEntity user);

}
