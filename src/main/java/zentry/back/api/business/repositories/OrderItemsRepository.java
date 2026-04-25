package zentry.back.api.business.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zentry.back.api.business.models.OrderItems;
import zentry.back.api.business.models.OrderItems.OrderItemsId;

import java.util.List;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItems, OrderItemsId> {

    List<OrderItems> findByOrderId(Integer orderId);

    List<OrderItems> findByProductId(Integer productId);

    boolean existsByOrderIdAndProductId(Integer orderId, Integer productId);

    void deleteByOrderId(Integer orderId);
}
