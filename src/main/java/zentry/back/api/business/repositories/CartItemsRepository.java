package zentry.back.api.business.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zentry.back.api.business.models.CartItems;
import zentry.back.api.business.models.CartItems.CartItemsId;

import java.util.List;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItems, CartItemsId> {

    List<CartItems> findByCartId(Integer cartId);

    List<CartItems> findByProductId(Integer productId);

    boolean existsByCartIdAndProductId(Integer cartId, Integer productId);

    void deleteByCartId(Integer cartId);
}
