package com.warehouse.api.item.service;

import com.warehouse.api.common.dto.PaginatedResponse;
import com.warehouse.api.common.exception.DeleteConstraintException;
import com.warehouse.api.common.exception.ResourceNotFoundException;
import com.warehouse.api.item.dto.ItemRequest;
import com.warehouse.api.item.dto.ItemResponse;
import com.warehouse.api.item.entity.Item;
import com.warehouse.api.item.mapper.ItemMapper;
import com.warehouse.api.item.repository.ItemRepository;
import com.warehouse.api.stock.repository.StockRepository;
import com.warehouse.api.variant.dto.VariantResponse;
import com.warehouse.api.variant.entity.Variant;
import com.warehouse.api.variant.repository.VariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final VariantRepository variantRepository;
    private final StockRepository stockRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ItemResponse> getAllItems(
            int page,
            int size,
            String sort,
            String order,
            String search,
            Integer minStock
    ) {
        String sortField = mapSortField(sort);
        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page - 1, Math.min(size, 100), Sort.by(direction, sortField));

        Page<Item> itemPage = itemRepository.findWithFilters(search, minStock, pageable);

        List<ItemResponse> responses = itemPage.getContent().stream()
                .map(this::mapToResponseWithDetails)
                .toList();

        Map<String, Object> filters = new HashMap<>();
        if (search != null) filters.put("search", search);
        if (minStock != null) filters.put("minStock", minStock);

        return PaginatedResponse.of(
                responses,
                page,
                size,
                itemPage.getTotalElements(),
                sort,
                order,
                filters.isEmpty() ? null : filters
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponse getItemById(@NonNull Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", id));
        return mapToResponseWithDetails(item);
    }

    @Override
    @Transactional
    public ItemResponse createItem(@NonNull ItemRequest request) {
        Item item = itemMapper.toEntity(request);
        Item saved = itemRepository.save(item);
        return mapToResponseWithDetails(saved);
    }

    @Override
    @Transactional
    public ItemResponse updateItem(@NonNull Long id, @NonNull ItemRequest request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", id));
        itemMapper.updateEntity(request, item);
        Item updated = itemRepository.save(item);
        return mapToResponseWithDetails(updated);
    }

    @Override
    @Transactional
    public void deleteItem(@NonNull Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", id));

        int totalStock = calculateTotalStock(id);
        if (totalStock > 0) {
            throw new DeleteConstraintException("Cannot delete item with existing stock");
        }

        variantRepository.deleteByItemId(id);
        stockRepository.deleteByItemId(id);
        itemRepository.delete(item);
    }

    private ItemResponse mapToResponseWithDetails(@NonNull Item item) {
        ItemResponse response = itemMapper.toResponse(item);
        Long itemId = item.getId();

        List<Variant> variants = variantRepository.findByItemId(itemId);
        List<VariantResponse> variantResponses = variants.stream()
                .map(v -> {
                    Integer stock = stockRepository.findQuantityByVariantId(v.getId()).orElse(0);
                    return VariantResponse.builder()
                            .id(v.getId())
                            .name(v.getName())
                            .sku(v.getSku())
                            .price(v.getPrice() != null ? v.getPrice() : item.getBasePrice())
                            .stock(stock)
                            .build();
                })
                .toList();

        response.setVariants(variantResponses);
        response.setTotalStock(calculateTotalStock(itemId));
        return response;
    }

    private int calculateTotalStock(Long itemId) {
        Integer itemStock = stockRepository.findQuantityByItemId(itemId).orElse(0);
        Integer variantStock = stockRepository.sumQuantityByItemIdThroughVariants(itemId);
        return itemStock + (variantStock != null ? variantStock : 0);
    }

    private String mapSortField(String sort) {
        return switch (sort) {
            case "name" -> "name";
            case "basePrice" -> "basePrice";
            default -> "createdAt";
        };
    }
}
