package com.warehouse.api.variant.controller;

import com.warehouse.api.common.dto.ApiResponse;
import com.warehouse.api.variant.dto.VariantRequest;
import com.warehouse.api.variant.dto.VariantResponse;
import com.warehouse.api.variant.service.VariantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Variants", description = "Variant management endpoints")
public class VariantController {

    private final VariantService variantService;

    @GetMapping("/api/v1/variants")
    @Operation(summary = "Get all variants")
    public ResponseEntity<ApiResponse<java.util.List<VariantResponse>>> getAllVariants() {
        java.util.List<VariantResponse> variants = variantService.getAllVariants();
        return ResponseEntity.ok(ApiResponse.success("Variants retrieved successfully", variants));
    }

    @GetMapping("/api/v1/variants/{id}")
    @Operation(summary = "Get variant by ID")
    public ResponseEntity<ApiResponse<VariantResponse>> getVariantById(@PathVariable Long id) {
        VariantResponse variant = variantService.getVariantById(id);
        return ResponseEntity.ok(ApiResponse.success("Variant retrieved successfully", variant));
    }

    @GetMapping("/api/v1/items/{itemId}/variants")
    @Operation(summary = "Get all variants for a specific item")
    public ResponseEntity<ApiResponse<java.util.List<VariantResponse>>> getVariantsByItemId(@PathVariable Long itemId) {
        java.util.List<VariantResponse> variants = variantService.getVariantsByItemId(itemId);
        return ResponseEntity.ok(ApiResponse.success("Variants retrieved successfully", variants));
    }

    @PostMapping("/api/v1/items/{itemId}/variants")
    @Operation(summary = "Create a new variant for an item")
    public ResponseEntity<ApiResponse<VariantResponse>> createVariant(
            @PathVariable Long itemId,
            @Valid @RequestBody VariantRequest request
    ) {
        VariantResponse variant = variantService.createVariant(itemId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Variant created successfully", variant));
    }

    @PutMapping("/api/v1/variants/{id}")
    @Operation(summary = "Update an existing variant")
    public ResponseEntity<ApiResponse<VariantResponse>> updateVariant(
            @PathVariable Long id,
            @Valid @RequestBody VariantRequest request
    ) {
        VariantResponse variant = variantService.updateVariant(id, request);
        return ResponseEntity.ok(ApiResponse.success("Variant updated successfully", variant));
    }

    @DeleteMapping("/api/v1/variants/{id}")
    @Operation(summary = "Delete a variant")
    public ResponseEntity<ApiResponse<Void>> deleteVariant(@PathVariable Long id) {
        variantService.deleteVariant(id);
        return ResponseEntity.ok(ApiResponse.success("Variant deleted successfully"));
    }
}
