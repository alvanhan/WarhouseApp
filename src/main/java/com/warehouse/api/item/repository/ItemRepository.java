package com.warehouse.api.item.repository;

import com.warehouse.api.item.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Item> findByNameContainingIgnoreCase(@Param("search") String search, Pageable pageable);

    @Query("""
            SELECT DISTINCT i FROM Item i 
            LEFT JOIN Variant v ON v.item.id = i.id 
            LEFT JOIN Stock s ON (s.item.id = i.id OR s.variant.id = v.id)
            WHERE (:search IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :search, '%')))
            GROUP BY i.id
            HAVING (:minStock IS NULL OR COALESCE(SUM(s.quantity), 0) >= :minStock)
            """)
    Page<Item> findWithFilters(
            @Param("search") String search,
            @Param("minStock") Integer minStock,
            Pageable pageable
    );
}
