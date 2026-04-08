package com.warehouse.api.stock.service;

import com.warehouse.api.common.dto.PaginatedResponse;
import com.warehouse.api.common.exception.InsufficientStockException;
import com.warehouse.api.common.exception.ResourceNotFoundException;
import com.warehouse.api.item.entity.Item;
import com.warehouse.api.item.repository.ItemRepository;
import com.warehouse.api.stock.dto.*;
import com.warehouse.api.stock.entity.Stock;
import com.warehouse.api.stock.mapper.StockMapper;
import com.warehouse.api.stock.repository.StockRepository;
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
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final ItemRepository itemRepository;
    private final VariantRepository variantRepository;
    private final StockMapper stockMapper;

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<StockResponse> getAllStock(
            int page,
            int size,
            String sort,
            String order,
            String search,
            String stockStatus,
            int lowStockThreshold
    ) {
        String sortField = mapSortField(sort);
        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page - 1, Math.min(size, 100), Sort.by(direction, sortField));

        Page<Stock> stockPage = stockRepository.findAllWithFilters(search, pageable);

        List<StockResponse> responses = stockPage.getContent().stream()
                .map(stock -> {
                    StockResponse response = stockMapper.toResponse(stock);
                    response.setStockStatus(calculateStockStatus(stock.getQuantity(), lowStockThreshold));
                    return response;
                })
                .filter(response -> stockStatus == null || matchesStockStatus(response, stockStatus, lowStockThreshold))
                .toList();

        Map<String, Object> filters = new HashMap<>();
        if (search != null) filters.put("search", search);
        if (stockStatus != null) filters.put("stockStatus", stockStatus);
        filters.put("lowStockThreshold", lowStockThreshold);

        return PaginatedResponse.of(
                responses,
                page,
                size,
                stockPage.getTotalElements(),
                sort,
                order,
                filters
        );
    }

    @Override
    @Transactional
    public StockResponse updateItemStock(@NonNull Long itemId, @NonNull StockRequest request) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item", itemId));

        Stock stock = stockRepository.findByItemId(itemId)
                .orElseGet(() -> Stock.builder()
                        .item(item)
                        .quantity(0)
                        .build());

        stock.setQuantity(request.getQuantity());
        Stock saved = stockRepository.save(stock);

        return StockResponse.builder()
                .itemId(itemId)
                .itemName(item.getName())
                .quantity(saved.getQuantity())
                .stockStatus(calculateStockStatus(saved.getQuantity(), 5))
                .build();
    }

    @Override
    @Transactional
    public StockResponse updateVariantStock(@NonNull Long variantId, @NonNull StockRequest request) {
        Variant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant", variantId));

        Stock stock = stockRepository.findByVariantId(variantId)
                .orElseGet(() -> Stock.builder()
                        .variant(variant)
                        .quantity(0)
                        .build());

        stock.setQuantity(request.getQuantity());
        Stock saved = stockRepository.save(stock);

        return StockResponse.builder()
                .itemId(variant.getItem().getId())
                .itemName(variant.getItem().getName())
                .variantId(variantId)
                .variantName(variant.getName())
                .sku(variant.getSku())
                .quantity(saved.getQuantity())
                .stockStatus(calculateStockStatus(saved.getQuantity(), 5))
                .build();
    }

    @Override
    @Transactional
    public SellResponse sell(@NonNull SellRequest request) {
        if (request.getVariantId() == null && request.getItemId() == null) {
            throw new IllegalArgumentException("Either variantId or itemId must be provided");
        }

        Stock stock;
        if (request.getVariantId() != null) {
            stock = stockRepository.findByVariantIdWithLock(request.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Stock for variant", request.getVariantId()));
        } else {
            stock = stockRepository.findByItemIdWithLock(request.getItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Stock for item", request.getItemId()));
        }

        if (stock.getQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(stock.getQuantity(), request.getQuantity());
        }

        stock.setQuantity(stock.getQuantity() - request.getQuantity());
        Stock saved = stockRepository.save(stock);

        return SellResponse.builder()
                .remaining(saved.getQuantity())
                .build();
    }

    private String calculateStockStatus(int quantity, int lowStockThreshold) {
        if (quantity == 0) {
            return "out_of_stock";
        } else if (quantity <= lowStockThreshold) {
            return "low_stock";
        } else {
            return "in_stock";
        }
    }

    private boolean matchesStockStatus(StockResponse response, String status, int threshold) {
        return switch (status) {
            case "in_stock" -> response.getQuantity() > threshold;
            case "low_stock" -> response.getQuantity() > 0 && response.getQuantity() <= threshold;
            case "out_of_stock" -> response.getQuantity() == 0;
            default -> true;
        };
    }

    private String mapSortField(String sort) {
        return switch (sort) {
            case "quantity" -> "quantity";
            case "itemName" -> "item.name";
            default -> "updatedAt";
        };
    }
}
