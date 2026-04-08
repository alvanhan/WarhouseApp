package com.warehouse.api.stock.repository;

import com.warehouse.api.stock.entity.Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stock s WHERE s.variant.id = :variantId")
    Optional<Stock> findByVariantIdWithLock(@Param("variantId") Long variantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stock s WHERE s.item.id = :itemId AND s.variant IS NULL")
    Optional<Stock> findByItemIdWithLock(@Param("itemId") Long itemId);

    @Query("SELECT s FROM Stock s WHERE s.variant.id = :variantId")
    Optional<Stock> findByVariantId(@Param("variantId") Long variantId);

    @Query("SELECT s FROM Stock s WHERE s.item.id = :itemId AND s.variant IS NULL")
    Optional<Stock> findByItemId(@Param("itemId") Long itemId);

    @Query("SELECT s.quantity FROM Stock s WHERE s.variant.id = :variantId")
    Optional<Integer> findQuantityByVariantId(@Param("variantId") Long variantId);

    @Query("SELECT s.quantity FROM Stock s WHERE s.item.id = :itemId AND s.variant IS NULL")
    Optional<Integer> findQuantityByItemId(@Param("itemId") Long itemId);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s WHERE s.variant.item.id = :itemId")
    Integer sumQuantityByItemIdThroughVariants(@Param("itemId") Long itemId);

    @Modifying
    @Query("DELETE FROM Stock s WHERE s.item.id = :itemId")
    void deleteByItemId(@Param("itemId") Long itemId);

    @Modifying
    @Query("DELETE FROM Stock s WHERE s.variant.id = :variantId")
    void deleteByVariantId(@Param("variantId") Long variantId);

    @Query("""
            SELECT s FROM Stock s 
            LEFT JOIN FETCH s.item i 
            LEFT JOIN FETCH s.variant v 
            LEFT JOIN FETCH v.item vi
            WHERE (:search IS NULL OR 
                   LOWER(COALESCE(i.name, vi.name)) LIKE LOWER(CONCAT('%', :search, '%')) OR 
                   LOWER(v.sku) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Stock> findAllWithFilters(@Param("search") String search, Pageable pageable);
}
