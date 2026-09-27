package zentry.back.api.core.repositories;

import zentry.back.api.core.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUsername(String username);
    List<Wallet> findByUsernameIn(java.util.Collection<String> usernames);
}
