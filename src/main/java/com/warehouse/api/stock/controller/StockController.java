package com.warehouse.api.stock.controller;

import com.warehouse.api.common.dto.ApiResponse;
import com.warehouse.api.common.dto.PaginatedResponse;
import com.warehouse.api.stock.dto.*;
import com.warehouse.api.stock.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "Stock management endpoints")
public class StockController {

    private final StockService stockService;

    @GetMapping
    @Operation(summary = "Get all stock with pagination and filtering")
    public ResponseEntity<PaginatedResponse<StockResponse>> getAllStock(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String stockStatus,
            @RequestParam(defaultValue = "5") int lowStockThreshold
    ) {
        PaginatedResponse<StockResponse> response = stockService.getAllStock(
                page, size, sort, order, search, stockStatus, lowStockThreshold
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update stock for an item without variants")
    public ResponseEntity<ApiResponse<StockResponse>> updateItemStock(
            @PathVariable Long itemId,
            @Valid @RequestBody StockRequest request
    ) {
        StockResponse stock = stockService.updateItemStock(itemId, request);
        return ResponseEntity.ok(ApiResponse.success("Stock updated successfully", stock));
    }

    @PutMapping("/variants/{variantId}")
    @Operation(summary = "Update stock for a variant")
    public ResponseEntity<ApiResponse<StockResponse>> updateVariantStock(
            @PathVariable Long variantId,
            @Valid @RequestBody StockRequest request
    ) {
        StockResponse stock = stockService.updateVariantStock(variantId, request);
        return ResponseEntity.ok(ApiResponse.success("Stock updated successfully", stock));
    }

    @PostMapping("/sell")
    @Operation(summary = "Process a sale - deduct stock")
    public ResponseEntity<ApiResponse<SellResponse>> sell(@Valid @RequestBody SellRequest request) {
        SellResponse response = stockService.sell(request);
        return ResponseEntity.ok(ApiResponse.success("Sell processed successfully", response));
    }
}
