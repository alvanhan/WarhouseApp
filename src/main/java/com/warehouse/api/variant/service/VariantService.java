package com.warehouse.api.variant.service;

import com.warehouse.api.variant.dto.VariantRequest;
import com.warehouse.api.variant.dto.VariantResponse;

import java.util.List;

public interface VariantService {

    List<VariantResponse> getAllVariants();

    VariantResponse getVariantById(Long id);

    List<VariantResponse> getVariantsByItemId(Long itemId);

    VariantResponse createVariant(Long itemId, VariantRequest request);

    VariantResponse updateVariant(Long id, VariantRequest request);

    void deleteVariant(Long id);
}
