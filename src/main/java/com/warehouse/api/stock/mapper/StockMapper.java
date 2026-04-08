package com.warehouse.api.stock.mapper;

import com.warehouse.api.stock.dto.StockResponse;
import com.warehouse.api.stock.entity.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface StockMapper {

    @Mapping(target = "itemId", source = "stock", qualifiedByName = "extractItemId")
    @Mapping(target = "itemName", source = "stock", qualifiedByName = "extractItemName")
    @Mapping(target = "variantId", source = "variant.id")
    @Mapping(target = "variantName", source = "variant.name")
    @Mapping(target = "sku", source = "variant.sku")
    @Mapping(target = "stockStatus", ignore = true)
    StockResponse toResponse(Stock stock);

    @Named("extractItemId")
    default Long extractItemId(Stock stock) {
        if (stock.getItem() != null) {
            return stock.getItem().getId();
        }
        if (stock.getVariant() != null && stock.getVariant().getItem() != null) {
            return stock.getVariant().getItem().getId();
        }
        return null;
    }

    @Named("extractItemName")
    default String extractItemName(Stock stock) {
        if (stock.getItem() != null) {
            return stock.getItem().getName();
        }
        if (stock.getVariant() != null && stock.getVariant().getItem() != null) {
            return stock.getVariant().getItem().getName();
        }
        return null;
    }
}
