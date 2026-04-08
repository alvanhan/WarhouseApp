package com.warehouse.api.variant;

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
import com.warehouse.api.variant.service.VariantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VariantServiceTest {

    @Mock
    private VariantRepository variantRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private VariantMapper variantMapper;

    @InjectMocks
    private VariantServiceImpl variantService;

    private Item testItem;
    private Variant testVariant;
    private VariantRequest testRequest;
    private VariantResponse testResponse;

    @BeforeEach
    void setUp() {
        testItem = Item.builder()
                .id(1L)
                .name("Test Item")
                .basePrice(new BigDecimal("100000"))
                .build();

        testVariant = Variant.builder()
                .id(1L)
                .item(testItem)
                .name("Test Variant")
                .sku("TEST-SKU")
                .price(new BigDecimal("110000"))
                .build();

        testRequest = VariantRequest.builder()
                .name("Test Variant")
                .sku("TEST-SKU")
                .price(new BigDecimal("110000"))
                .build();

        testResponse = VariantResponse.builder()
                .id(1L)
                .name("Test Variant")
                .sku("TEST-SKU")
                .price(new BigDecimal("110000"))
                .stock(0)
                .build();
    }

    @Test
    void createVariant_ShouldReturnCreatedVariant() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(variantRepository.existsBySku("TEST-SKU")).thenReturn(false);
        when(variantMapper.toEntity(testRequest)).thenReturn(testVariant);
        when(variantRepository.save(any(Variant.class))).thenReturn(testVariant);
        when(variantMapper.toResponse(testVariant)).thenReturn(testResponse);

        VariantResponse result = variantService.createVariant(1L, testRequest);

        assertNotNull(result);
        assertEquals("Test Variant", result.getName());
        assertEquals("TEST-SKU", result.getSku());
        verify(variantRepository).save(any(Variant.class));
    }

    @Test
    void createVariant_ShouldThrowException_WhenItemNotFound() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> variantService.createVariant(999L, testRequest));
    }

    @Test
    void createVariant_ShouldThrowException_WhenSkuExists() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(variantRepository.existsBySku("TEST-SKU")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> variantService.createVariant(1L, testRequest));
    }

    @Test
    void updateVariant_ShouldReturnUpdatedVariant() {
        when(variantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(variantRepository.save(any(Variant.class))).thenReturn(testVariant);
        when(stockRepository.findQuantityByVariantId(1L)).thenReturn(Optional.of(5));
        when(variantMapper.toResponse(testVariant)).thenReturn(testResponse);

        VariantResponse result = variantService.updateVariant(1L, testRequest);

        assertNotNull(result);
        verify(variantMapper).updateEntity(any(), any());
    }

    @Test
    void updateVariant_ShouldThrowException_WhenNotFound() {
        when(variantRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> variantService.updateVariant(999L, testRequest));
    }

    @Test
    void deleteVariant_ShouldDeleteVariant_WhenNoStock() {
        when(variantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(stockRepository.findQuantityByVariantId(1L)).thenReturn(Optional.of(0));

        variantService.deleteVariant(1L);

        verify(variantRepository).delete(testVariant);
    }

    @Test
    void deleteVariant_ShouldThrowException_WhenHasStock() {
        when(variantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(stockRepository.findQuantityByVariantId(1L)).thenReturn(Optional.of(10));

        assertThrows(DeleteConstraintException.class,
                () -> variantService.deleteVariant(1L));
        verify(variantRepository, never()).delete(any());
    }
}
