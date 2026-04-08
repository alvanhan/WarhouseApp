package com.warehouse.api.item.mapper;

import com.warehouse.api.item.dto.ItemRequest;
import com.warehouse.api.item.dto.ItemResponse;
import com.warehouse.api.item.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Item toEntity(ItemRequest request);

    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "totalStock", ignore = true)
    ItemResponse toResponse(Item item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ItemRequest request, @MappingTarget Item item);
}
