package by.nuray.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.stream.Collectors;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByOwnerId(int ownerId);







    @Query("SELECT i FROM Item i WHERE (i.name ILIKE CONCAT('%', :query, '%') " +
            "OR i.description ILIKE CONCAT('%', :query, '%')) " +
            "AND i.available = true")
    List<Item> searchAvailableItems(@Param("query") String query);





}
