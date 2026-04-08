package com.warehouse.api.item.controller;

import com.warehouse.api.common.dto.ApiResponse;
import com.warehouse.api.common.dto.PaginatedResponse;
import com.warehouse.api.item.dto.ItemRequest;
import com.warehouse.api.item.dto.ItemResponse;
import com.warehouse.api.item.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
@Tag(name = "Items", description = "Item management endpoints")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    @Operation(summary = "Get all items with pagination and filtering")
    public ResponseEntity<PaginatedResponse<ItemResponse>> getAllItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer minStock
    ) {
        PaginatedResponse<ItemResponse> response = itemService.getAllItems(page, size, sort, order, search, minStock);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by ID")
    public ResponseEntity<ApiResponse<ItemResponse>> getItemById(@PathVariable Long id) {
        ItemResponse item = itemService.getItemById(id);
        return ResponseEntity.ok(ApiResponse.success("Item fetched successfully", item));
    }

    @PostMapping
    @Operation(summary = "Create a new item")
    public ResponseEntity<ApiResponse<ItemResponse>> createItem(@Valid @RequestBody ItemRequest request) {
        ItemResponse item = itemService.createItem(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Item created successfully", item));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing item")
    public ResponseEntity<ApiResponse<ItemResponse>> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemRequest request
    ) {
        ItemResponse item = itemService.updateItem(id, request);
        return ResponseEntity.ok(ApiResponse.success("Item updated successfully", item));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an item")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok(ApiResponse.success("Item deleted successfully"));
    }
}
