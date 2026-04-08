package com.warehouse.api.variant.mapper;

import com.warehouse.api.variant.dto.VariantRequest;
import com.warehouse.api.variant.dto.VariantResponse;
import com.warehouse.api.variant.entity.Variant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VariantMapper {

    @Mapping(target = "item", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Variant toEntity(VariantRequest request);

    @Mapping(target = "stock", ignore = true)
    VariantResponse toResponse(Variant variant);

    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(VariantRequest request, @MappingTarget Variant variant);
}
