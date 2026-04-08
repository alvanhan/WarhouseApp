package com.warehouse.api.stock.service;

import com.warehouse.api.common.dto.PaginatedResponse;
import com.warehouse.api.stock.dto.*;

public interface StockService {

    PaginatedResponse<StockResponse> getAllStock(
            int page,
            int size,
            String sort,
            String order,
            String search,
            String stockStatus,
            int lowStockThreshold
    );

    StockResponse updateItemStock(Long itemId, StockRequest request);

    StockResponse updateVariantStock(Long variantId, StockRequest request);

    SellResponse sell(SellRequest request);
}
