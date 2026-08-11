package zentry.back.api.core.repositories;

import zentry.back.api.core.models.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    //Para buscar usuarios por email
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

}