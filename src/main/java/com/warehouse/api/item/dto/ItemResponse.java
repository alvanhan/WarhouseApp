package com.warehouse.api.item.dto;

import com.warehouse.api.variant.dto.VariantResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private List<VariantResponse> variants;
    private Integer totalStock;
}
