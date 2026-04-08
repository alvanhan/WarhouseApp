package com.warehouse.api.item.mapper;

import com.warehouse.api.item.dto.ItemRequest;
import com.warehouse.api.item.dto.ItemResponse;
import com.warehouse.api.item.entity.Item;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-08T11:51:50+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Eclipse Adoptium)"
)
@Component
public class ItemMapperImpl implements ItemMapper {

    @Override
    public Item toEntity(ItemRequest request) {
        if ( request == null ) {
            return null;
        }

        Item.ItemBuilder item = Item.builder();

        item.name( request.getName() );
        item.description( request.getDescription() );
        item.basePrice( request.getBasePrice() );

        return item.build();
    }

    @Override
    public ItemResponse toResponse(Item item) {
        if ( item == null ) {
            return null;
        }

        ItemResponse.ItemResponseBuilder itemResponse = ItemResponse.builder();

        itemResponse.id( item.getId() );
        itemResponse.name( item.getName() );
        itemResponse.description( item.getDescription() );
        itemResponse.basePrice( item.getBasePrice() );

        return itemResponse.build();
    }

    @Override
    public void updateEntity(ItemRequest request, Item item) {
        if ( request == null ) {
            return;
        }

        item.setName( request.getName() );
        item.setDescription( request.getDescription() );
        item.setBasePrice( request.getBasePrice() );
    }
}
