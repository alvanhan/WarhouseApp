package com.warehouse.api.variant.service;

import com.warehouse.api.common.exception.DeleteConstraintException;
import com.warehouse.api.common.exception.DuplicateResourceException;
import com.warehouse.api.common.exception.ResourceNotFoundException;
import com.warehouse.api.item.entity.Item;
import com.warehouse.api.item.repository.ItemRepository;
import com.warehouse.api.stock.repository.StockRepository;
import com.warehouse.api.variant.dto.VariantRequest;
import com.warehouse.api.variant.dto.VariantResponse;
import com.warehouse.api.variant.entity.Variant;
import com.warehouse.api.variant.mapper.VariantMapper;
import com.warehouse.api.variant.repository.VariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VariantServiceImpl implements VariantService {

    private final VariantRepository variantRepository;
    private final ItemRepository itemRepository;
    private final StockRepository stockRepository;
    private final VariantMapper variantMapper;

    @Override
    @Transactional(readOnly = true)
    public java.util.List<VariantResponse> getAllVariants() {
        return variantRepository.findAll().stream()
                .map(variant -> {
                    VariantResponse response = variantMapper.toResponse(variant);
                    Integer stock = stockRepository.findQuantityByVariantId(variant.getId()).orElse(0);
                    response.setPrice(variant.getPrice() != null ? variant.getPrice() : variant.getItem().getBasePrice());
                    response.setStock(stock);
                    return response;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VariantResponse getVariantById(@NonNull Long id) {
        Variant variant = variantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variant", id));
        
        Integer stock = stockRepository.findQuantityByVariantId(id).orElse(0);
        VariantResponse response = variantMapper.toResponse(variant);
        response.setPrice(variant.getPrice() != null ? variant.getPrice() : variant.getItem().getBasePrice());
        response.setStock(stock);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<VariantResponse> getVariantsByItemId(@NonNull Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ResourceNotFoundException("Item", itemId);
        }

        return variantRepository.findByItemId(itemId).stream()
                .map(variant -> {
                    VariantResponse response = variantMapper.toResponse(variant);
                    Integer stock = stockRepository.findQuantityByVariantId(variant.getId()).orElse(0);
                    response.setPrice(variant.getPrice() != null ? variant.getPrice() : variant.getItem().getBasePrice());
                    response.setStock(stock);
                    return response;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public VariantResponse createVariant(@NonNull Long itemId, @NonNull VariantRequest request) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item", itemId));

        if (variantRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("SKU already exists");
        }

        Variant variant = variantMapper.toEntity(request);
        variant.setItem(item);
        Variant saved = variantRepository.save(variant);

        VariantResponse response = variantMapper.toResponse(saved);
        response.setPrice(saved.getPrice() != null ? saved.getPrice() : item.getBasePrice());
        response.setStock(0);
        return response;
    }

    @Override
    @Transactional
    public VariantResponse updateVariant(@NonNull Long id, @NonNull VariantRequest request) {
        Variant variant = variantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variant", id));

        variantMapper.updateEntity(request, variant);
        Variant updated = variantRepository.save(variant);

        Integer stock = stockRepository.findQuantityByVariantId(id).orElse(0);
        VariantResponse response = variantMapper.toResponse(updated);
        response.setPrice(updated.getPrice() != null ? updated.getPrice() : variant.getItem().getBasePrice());
        response.setStock(stock);
        return response;
    }

    @Override
    @Transactional
    public void deleteVariant(@NonNull Long id) {
        Variant variant = variantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variant", id));

        Integer stock = stockRepository.findQuantityByVariantId(id).orElse(0);
        if (stock > 0) {
            throw new DeleteConstraintException("Cannot delete variant with existing stock");
        }

        stockRepository.deleteByVariantId(id);
        variantRepository.delete(variant);
    }
}
