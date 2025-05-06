package com.nguyen.fashion.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
/**
 * Repository for recording and retrieving price history entries.
 */
@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    /**
     * Find all price history entries for a WatchItem, ordered by checkedAt descending.
     */
    List<PriceHistory> findByWatchItemOrderByCheckedAtDesc(WatchItem watchItem);

    /**
     * Find the two most recent price history entries for a WatchItem.
     */
    List<PriceHistory> findTop2ByWatchItemOrderByCheckedAtDesc(WatchItem watchItem);
}
