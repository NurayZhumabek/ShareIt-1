package by.nuray.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findAllByRequestorIdOrderByCreatedAtDesc(int userId);


    @Query(value = "SELECT * FROM requests WHERE requestor_id != :userId ORDER BY created_at DESC LIMIT :size OFFSET :from", nativeQuery = true)
    List<ItemRequest> findRequestsFromOthers(@Param("userId") int userId,
                                             @Param("from") int from,
                                             @Param("size") int size);

}
