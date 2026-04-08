package com.warehouse.api.item.service;

import com.warehouse.api.common.dto.PaginatedResponse;
import com.warehouse.api.item.dto.ItemRequest;
import com.warehouse.api.item.dto.ItemResponse;

public interface ItemService {

    PaginatedResponse<ItemResponse> getAllItems(
            int page,
            int size,
            String sort,
            String order,
            String search,
            Integer minStock
    );

    ItemResponse getItemById(Long id);

    ItemResponse createItem(ItemRequest request);

    ItemResponse updateItem(Long id, ItemRequest request);

    void deleteItem(Long id);
}
