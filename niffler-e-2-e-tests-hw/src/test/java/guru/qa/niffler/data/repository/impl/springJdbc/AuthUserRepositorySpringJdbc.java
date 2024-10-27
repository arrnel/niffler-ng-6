package guru.qa.niffler.data.repository.impl.springJdbc;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.springJdbc.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

    private AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
    private AuthAuthorityDao authorityDao = new AuthAuthorityDaoSpringJdbc();

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        var createdUser = authUserDao.create(user);
        authorityDao.create(
                user.getAuthorities().stream()
                        .map(authorityEntity -> authorityEntity.setUser(createdUser))
                        .toArray(AuthAuthorityEntity[]::new));
        return createdUser
                .setAuthorities(authorityDao.findByUserId(createdUser.getId()));
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        return authUserDao.findById(id)
                .map(userEntity ->
                        userEntity.setAuthorities(
                                authorityDao.findByUserId(userEntity.getId())));
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        return authUserDao.findByUsername(username)
                .map(userEntity ->
                        userEntity.setAuthorities(
                                authorityDao.findByUserId(userEntity.getId())));
    }

    @Override
    public List<AuthUserEntity> findAll() {
        return authUserDao.findAll()
                .stream()
                .map(userEntity ->
                        userEntity.setAuthorities(
                                authorityDao.findByUserId(userEntity.getId())))
                .toList();
    }

    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        authorityDao.update(
                user.getAuthorities()
                        .toArray(new AuthAuthorityEntity[0]));
        return authUserDao.update(
                user.setAuthorities(
                        authorityDao.findByUserId(user.getId())));
    }

    @Override
    public void remove(AuthUserEntity user) {
        authorityDao.remove(
                authorityDao.findByUserId(user.getId())
                        .toArray(AuthAuthorityEntity[]::new));
        authUserDao.remove(user);
    }

}