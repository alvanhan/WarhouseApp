package com.warehouse.api.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {

    private Long itemId;
    private String itemName;
    private Long variantId;
    private String variantName;
    private String sku;
    private Integer quantity;
    private String stockStatus;
}
