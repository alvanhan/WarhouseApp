package com.warehouse.api.variant.mapper;

import com.warehouse.api.variant.dto.VariantRequest;
import com.warehouse.api.variant.dto.VariantResponse;
import com.warehouse.api.variant.entity.Variant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-08T11:51:50+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Eclipse Adoptium)"
)
@Component
public class VariantMapperImpl implements VariantMapper {

    @Override
    public Variant toEntity(VariantRequest request) {
        if ( request == null ) {
            return null;
        }

        Variant.VariantBuilder variant = Variant.builder();

        variant.name( request.getName() );
        variant.sku( request.getSku() );
        variant.price( request.getPrice() );

        return variant.build();
    }

    @Override
    public VariantResponse toResponse(Variant variant) {
        if ( variant == null ) {
            return null;
        }

        VariantResponse.VariantResponseBuilder variantResponse = VariantResponse.builder();

        variantResponse.id( variant.getId() );
        variantResponse.name( variant.getName() );
        variantResponse.sku( variant.getSku() );
        variantResponse.price( variant.getPrice() );

        return variantResponse.build();
    }

    @Override
    public void updateEntity(VariantRequest request, Variant variant) {
        if ( request == null ) {
            return;
        }

        variant.setName( request.getName() );
        variant.setPrice( request.getPrice() );
    }
}
