package com.pragma.bootcamp.r2dbc;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

import com.pragma.bootcamp.model.user.User;
import com.pragma.bootcamp.model.user.gateways.UserRepository;
import com.pragma.bootcamp.r2dbc.entity.UserEntity;
import com.pragma.bootcamp.r2dbc.helper.ReactiveAdapterOperations;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class UserRepositoryAdapter extends ReactiveAdapterOperations<User, UserEntity, Long, UserReactiveRepository>
        implements UserRepository {
    public UserRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, User.class));
    }

    @Override
    public Flux<User> getAll() {

        return super.findAll();
    }

    @Override
    public Mono<User> update(User userUserUpdate) {

        return super.save(userUserUpdate);
    }

    @Override
    public Mono<Void> delete(Long idUser) {

        return repository.deleteById(idUser);
    }

    @Override
    public Mono<User> create(User userUser) {

        return super.save(userUser);

    }

}
