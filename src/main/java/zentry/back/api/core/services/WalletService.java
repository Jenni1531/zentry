package zentry.back.api.core.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import zentry.back.api.core.dtos.*;
import zentry.back.api.core.models.*;
import zentry.back.api.core.repositories.*;
import zentry.back.api.core.mappers.CoreMappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@SuppressWarnings("null")
public class WalletService {

    /** Regalo de bienvenida con el que se crea toda billetera */
    private static final BigDecimal WELCOME_GIFT = BigDecimal.valueOf(100);

    private final WalletRepository walletRepo;
    private final WalletTransactionRepository txRepo;
    private final UserRepository userRepo;
    private final GamificationEventService gamificationEventService;

    public WalletService(WalletRepository walletRepo, WalletTransactionRepository txRepo, UserRepository userRepo,
                          @org.springframework.context.annotation.Lazy GamificationEventService gamificationEventService) {
        this.walletRepo = walletRepo;
        this.txRepo = txRepo;
        this.userRepo = userRepo;
        this.gamificationEventService = gamificationEventService;
    }

    /**
     * Unifica billeteras duplicadas. Antes cada usuario podía tener una billetera con su email
     * (página de billetera, recargas, suscripciones) y otra con su @usuario (sidebar, misiones,
     * logros, tienda), así que el saldo no cuadraba. La clave única ahora es el email (no cambia).
     * Cada billetera extra se creó con 100 ZC de regalo; ese regalo duplicado se descuenta al fusionar.
     */
    @jakarta.annotation.PostConstruct
    public void consolidateWallets() {
        try {
            for (User user : userRepo.findAll()) {
                if (user.getEmail() == null) continue;
                String canonical = user.getEmail();
                java.util.Set<String> aliases = new java.util.LinkedHashSet<>();
                // getUsername() devuelve el email (contrato de UserDetails); el @usuario real es getHandle()
                if (user.getHandle() != null && !user.getHandle().isBlank()) aliases.add(user.getHandle());
                aliases.add(canonical.split("@")[0]);
                aliases.remove(canonical);
                if (aliases.isEmpty()) continue;

                List<Wallet> legacy = walletRepo.findByUsernameIn(aliases);
                if (legacy.isEmpty()) continue;

                Wallet main = walletRepo.findByUsername(canonical).orElse(null);
                BigDecimal total = legacy.stream().map(Wallet::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
                int duplicatedGifts = main != null ? legacy.size() : legacy.size() - 1;
                if (main != null) total = total.add(main.getBalance());
                total = total.subtract(WELCOME_GIFT.multiply(BigDecimal.valueOf(Math.max(0, duplicatedGifts)))).max(BigDecimal.ZERO);

                Wallet target = main != null ? main : legacy.get(0);
                String plan = legacy.stream().map(Wallet::getActivePlanId)
                        .filter(pl -> pl != null && !"free".equalsIgnoreCase(pl)).findFirst()
                        .orElse(target.getActivePlanId());
                for (Wallet w : legacy) {
                    if (w != target) walletRepo.delete(w);
                }
                walletRepo.flush();
                target.setUsername(canonical);
                target.setBalance(total);
                target.setActivePlanId(plan);
                walletRepo.save(target);

                List<WalletTransaction> txs = txRepo.findByUsernameIn(aliases);
                txs.forEach(t -> t.setUsername(canonical));
                txRepo.saveAll(txs);
            }
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(WalletService.class).warn("No se pudieron unificar billeteras: {}", e.getMessage());
        }
    }

    /** Traduce email / @usuario / prefijo del email a la clave única de la billetera (el email). */
    public String walletKey(String identifier) {
        if (identifier == null || identifier.isBlank()) return identifier;
        return userRepo.findByEmail(identifier)
                .or(() -> userRepo.findByUsername(identifier))
                .or(() -> userRepo.findByUsernameOrEmail(identifier, identifier))
                .map(User::getEmail)
                .orElse(identifier);
    }

    /** Monedas ganadas hoy (día local de la plataforma): ingresos + recargas, sin gastos */
    public BigDecimal earnedToday(String identifier) {
        java.time.LocalDateTime since = zentry.back.api.core.util.ZentryClock.today()
                .atStartOfDay(zentry.back.api.core.util.ZentryClock.zone())
                .withZoneSameInstant(java.time.ZoneId.systemDefault()).toLocalDateTime();
        BigDecimal sum = txRepo.sumIncomeSince(walletKey(identifier), since);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    private void trackWalletBalanceAchievement(String username, BigDecimal balance) {
        userRepo.findByEmail(username).ifPresent(u ->
                gamificationEventService.setAchievementProgressAbsolute(u.getId(), "wallet_balance", balance.intValue()));
    }

    public WalletResponse getWallet(String identifier) {
        String username = walletKey(identifier);
        Wallet wallet = getOrCreateWallet(username);
        List<WalletTransaction> transactions = txRepo.findByUsernameOrderByCreatedAtDesc(username);
        return CoreMappers.toResponse(wallet, transactions);
    }

    @Transactional
    public WalletResponse subscribe(String identifier, SubscribeRequest request) {
        String username = walletKey(identifier);
        Wallet wallet = getOrCreateWallet(username);
        String planId = request.getPlanId() != null ? request.getPlanId().toLowerCase() : "free";
        String cycle = request.getCycle() != null ? request.getCycle().toLowerCase() : "monthly";

        BigDecimal cost = calculatePlanCost(planId, cycle);

        if (wallet.getBalance().compareTo(cost) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente en Zentry Coins para adquirir el plan " + planId.toUpperCase());
        }

        if (cost.compareTo(BigDecimal.ZERO) > 0) {
            wallet.setBalance(wallet.getBalance().subtract(cost));
            txRepo.save(WalletTransaction.builder()
                    .username(username)
                    .type(TransactionType.EGRESO)
                    .amount(cost)
                    .description("Suscripción a Plan " + planId.toUpperCase() + " (" + cycle + ")")
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        wallet.setActivePlanId(planId);
        int daysToAdd = "annual".equalsIgnoreCase(cycle) ? 365 : 30;
        wallet.setNextBillingDate(LocalDateTime.now().plusDays(daysToAdd));
        walletRepo.save(wallet);

        return getWallet(username);
    }

    @Transactional
    public WalletResponse topup(String identifier, TopupRequest request) {
        String username = walletKey(identifier);
        Wallet wallet = getOrCreateWallet(username);
        BigDecimal amount = request.getAmount();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El monto de recarga debe ser mayor a 0 ZC");
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepo.save(wallet);

        txRepo.save(WalletTransaction.builder()
                .username(username)
                .type(TransactionType.RECARGA)
                .amount(amount)
                .description("Recarga de " + amount + " Zentry Coins (ZC)")
                .createdAt(LocalDateTime.now())
                .build());

        trackWalletBalanceAchievement(username, wallet.getBalance());

        return getWallet(username);
    }

    @Transactional
    public WalletResponse transfer(String senderIdentifier, TransferRequest request) {
        String senderUsername = walletKey(senderIdentifier);
        String recipient = request.getRecipientUsername();
        BigDecimal amount = request.getAmount();

        if (recipient == null || recipient.trim().equalsIgnoreCase(senderUsername.trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes transferir Zentry Coins a ti mismo");
        }

        User recipientUser = userRepo.findByUsername(recipient)
                .orElseGet(() -> userRepo.findByEmail(recipient)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario destinatario no existe")));

        String actualRecipientUsername = recipientUser.getUsername();
        Wallet senderWallet = getOrCreateWallet(senderUsername);

        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente para realizar la transferencia");
        }

        Wallet recipientWallet = getOrCreateWallet(actualRecipientUsername);

        // Descontar del emisor
        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        walletRepo.save(senderWallet);

        txRepo.save(WalletTransaction.builder()
                .username(senderUsername)
                .type(TransactionType.EGRESO)
                .amount(amount)
                .description("Transferencia enviada a " + actualRecipientUsername)
                .createdAt(LocalDateTime.now())
                .build());

        // Acreditar al receptor
        recipientWallet.setBalance(recipientWallet.getBalance().add(amount));
        walletRepo.save(recipientWallet);

        txRepo.save(WalletTransaction.builder()
                .username(actualRecipientUsername)
                .type(TransactionType.INGRESO)
                .amount(amount)
                .description("Transferencia recibida de " + userRepo.findByEmail(senderUsername).map(u -> "@" + u.getHandle()).orElse(senderUsername))
                .createdAt(LocalDateTime.now())
                .build());

        trackWalletBalanceAchievement(actualRecipientUsername, recipientWallet.getBalance());
        userRepo.findByEmail(senderUsername).ifPresent(u ->
                gamificationEventService.recordAchievementProgress(u.getId(), "transfer_coins", 1));

        return getWallet(senderUsername);
    }

    public Wallet getOrCreateWallet(String identifier) {
        String username = walletKey(identifier);
        return walletRepo.findByUsername(username)
                .orElseGet(() -> walletRepo.save(Wallet.builder()
                        .username(username)
                        .balance(WELCOME_GIFT)
                        .activePlanId("free")
                        .nextBillingDate(LocalDateTime.now().plusDays(30))
                        .build()));
    }

    private BigDecimal calculatePlanCost(String planId, String cycle) {
        boolean isAnnual = "annual".equalsIgnoreCase(cycle);
        return switch (planId.toLowerCase()) {
            case "pro" -> isAnnual ? BigDecimal.valueOf(2000.00) : BigDecimal.valueOf(200.00);
            case "vip" -> isAnnual ? BigDecimal.valueOf(5000.00) : BigDecimal.valueOf(500.00);
            default -> BigDecimal.ZERO;
        };
    }
}
