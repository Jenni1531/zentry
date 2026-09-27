package zentry.back.api.core.repositories;

import zentry.back.api.core.models.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> findByUsernameOrderByCreatedAtDesc(String username);
    List<WalletTransaction> findByUsernameIn(java.util.Collection<String> usernames);

    /** Suma de ingresos (INGRESO + RECARGA) de un usuario desde una fecha */
    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(t.amount), 0) FROM WalletTransaction t WHERE t.username = :username AND t.type <> zentry.back.api.core.models.TransactionType.EGRESO AND t.createdAt >= :since")
    java.math.BigDecimal sumIncomeSince(@org.springframework.data.repository.query.Param("username") String username,
                                        @org.springframework.data.repository.query.Param("since") java.time.LocalDateTime since);
}
