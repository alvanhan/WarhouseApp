package com.warehouse.api.stock.mapper;

import com.warehouse.api.stock.dto.StockResponse;
import com.warehouse.api.stock.entity.Stock;
import com.warehouse.api.variant.entity.Variant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-08T11:51:50+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Eclipse Adoptium)"
)
@Component
public class StockMapperImpl implements StockMapper {

    @Override
    public StockResponse toResponse(Stock stock) {
        if ( stock == null ) {
            return null;
        }

        StockResponse.StockResponseBuilder stockResponse = StockResponse.builder();

        stockResponse.itemId( extractItemId( stock ) );
        stockResponse.itemName( extractItemName( stock ) );
        stockResponse.variantId( stockVariantId( stock ) );
        stockResponse.variantName( stockVariantName( stock ) );
        stockResponse.sku( stockVariantSku( stock ) );
        stockResponse.quantity( stock.getQuantity() );

        return stockResponse.build();
    }

    private Long stockVariantId(Stock stock) {
        if ( stock == null ) {
            return null;
        }
        Variant variant = stock.getVariant();
        if ( variant == null ) {
            return null;
        }
        Long id = variant.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String stockVariantName(Stock stock) {
        if ( stock == null ) {
            return null;
        }
        Variant variant = stock.getVariant();
        if ( variant == null ) {
            return null;
        }
        String name = variant.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String stockVariantSku(Stock stock) {
        if ( stock == null ) {
            return null;
        }
        Variant variant = stock.getVariant();
        if ( variant == null ) {
            return null;
        }
        String sku = variant.getSku();
        if ( sku == null ) {
            return null;
        }
        return sku;
    }
}
