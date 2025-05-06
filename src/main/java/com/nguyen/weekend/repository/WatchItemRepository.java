package com.nguyen.weekend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WatchItemRepository extends JpaRepository<WatchItem, String> {
    List<WatchItem> findByCategoryAndColor(String category, String color);
}
