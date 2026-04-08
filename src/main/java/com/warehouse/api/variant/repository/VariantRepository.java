package com.warehouse.api.variant.repository;

import com.warehouse.api.variant.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VariantRepository extends JpaRepository<Variant, Long> {

    List<Variant> findByItemId(Long itemId);

    Optional<Variant> findBySku(String sku);

    boolean existsBySku(String sku);

    @Modifying
    @Query("DELETE FROM Variant v WHERE v.item.id = :itemId")
    void deleteByItemId(@Param("itemId") Long itemId);
}
