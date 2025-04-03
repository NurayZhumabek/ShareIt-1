package by.nuray.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findByOwnerId(int ownerId);

    List<Item> findByRequestId(int requestId);


    @Query(value = "SELECT * FROM items WHERE owner_id = :userId LIMIT :size OFFSET :from",
            nativeQuery = true)
    List<Item> findAllByOwnerIdPaged(@Param("userId") int userId,
                                     @Param("from") int from,
                                     @Param("size") int size);


    @Query(value = "SELECT * FROM items " +
            "WHERE (name ILIKE CONCAT('%', :query, '%') " +
            "OR description ILIKE CONCAT('%', :query, '%')) " +
            "AND is_available = true " +
            "LIMIT :size OFFSET :from", nativeQuery = true)
    List<Item> searchAvailableItemsPaged(@Param("query") String query,
                                         @Param("from") int from,
                                         @Param("size") int size);

    @Query(value = "SELECT * FROM items " +
            "WHERE (name ILIKE CONCAT('%', :query, '%') " +
            "OR description ILIKE CONCAT('%', :query, '%')) " +
            "AND is_available = true ", nativeQuery = true)
    List<Item> searchAvailableItems(@Param("query") String query);


}
