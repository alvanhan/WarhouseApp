package com.warehouse.api.item;

import com.warehouse.api.common.dto.PaginatedResponse;
import com.warehouse.api.common.exception.DeleteConstraintException;
import com.warehouse.api.common.exception.ResourceNotFoundException;
import com.warehouse.api.item.dto.ItemRequest;
import com.warehouse.api.item.dto.ItemResponse;
import com.warehouse.api.item.entity.Item;
import com.warehouse.api.item.mapper.ItemMapper;
import com.warehouse.api.item.repository.ItemRepository;
import com.warehouse.api.item.service.ItemServiceImpl;
import com.warehouse.api.stock.repository.StockRepository;
import com.warehouse.api.variant.repository.VariantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private VariantRepository variantRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item testItem;
    private ItemRequest testRequest;
    private ItemResponse testResponse;

    @BeforeEach
    void setUp() {
        testItem = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .basePrice(new BigDecimal("100000"))
                .build();

        testRequest = ItemRequest.builder()
                .name("Test Item")
                .description("Test Description")
                .basePrice(new BigDecimal("100000"))
                .build();

        testResponse = ItemResponse.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .basePrice(new BigDecimal("100000"))
                .build();
    }

    @Test
    void getAllItems_ShouldReturnPaginatedResponse() {
        Page<Item> itemPage = new PageImpl<>(Collections.singletonList(testItem));
        when(itemRepository.findWithFilters(any(), any(), any(Pageable.class))).thenReturn(itemPage);
        when(itemMapper.toResponse(any(Item.class))).thenReturn(testResponse);
        when(variantRepository.findByItemId(any())).thenReturn(Collections.emptyList());
        when(stockRepository.findQuantityByItemId(any())).thenReturn(Optional.of(0));
        when(stockRepository.sumQuantityByItemIdThroughVariants(any())).thenReturn(0);

        PaginatedResponse<ItemResponse> result = itemService.getAllItems(1, 10, "createdAt", "desc", null, null);

        assertNotNull(result);
        assertEquals("success", result.getStatus());
        assertEquals(1, result.getData().size());
    }

    @Test
    void getItemById_ShouldReturnItem_WhenExists() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(itemMapper.toResponse(testItem)).thenReturn(testResponse);
        when(variantRepository.findByItemId(1L)).thenReturn(Collections.emptyList());
        when(stockRepository.findQuantityByItemId(1L)).thenReturn(Optional.of(10));
        when(stockRepository.sumQuantityByItemIdThroughVariants(1L)).thenReturn(0);

        ItemResponse result = itemService.getItemById(1L);

        assertNotNull(result);
        assertEquals("Test Item", result.getName());
    }

    @Test
    void getItemById_ShouldThrowException_WhenNotExists() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.getItemById(999L));
    }

    @Test
    void createItem_ShouldReturnCreatedItem() {
        when(itemMapper.toEntity(testRequest)).thenReturn(testItem);
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);
        when(itemMapper.toResponse(testItem)).thenReturn(testResponse);
        when(variantRepository.findByItemId(any())).thenReturn(Collections.emptyList());
        when(stockRepository.findQuantityByItemId(any())).thenReturn(Optional.of(0));
        when(stockRepository.sumQuantityByItemIdThroughVariants(any())).thenReturn(0);

        ItemResponse result = itemService.createItem(testRequest);

        assertNotNull(result);
        assertEquals("Test Item", result.getName());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);
        when(itemMapper.toResponse(testItem)).thenReturn(testResponse);
        when(variantRepository.findByItemId(1L)).thenReturn(Collections.emptyList());
        when(stockRepository.findQuantityByItemId(1L)).thenReturn(Optional.of(0));
        when(stockRepository.sumQuantityByItemIdThroughVariants(1L)).thenReturn(0);

        ItemResponse result = itemService.updateItem(1L, testRequest);

        assertNotNull(result);
        verify(itemMapper).updateEntity(eq(testRequest), any(Item.class));
    }

    @Test
    void deleteItem_ShouldDeleteItem_WhenNoStock() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(stockRepository.findQuantityByItemId(1L)).thenReturn(Optional.of(0));
        when(stockRepository.sumQuantityByItemIdThroughVariants(1L)).thenReturn(0);

        itemService.deleteItem(1L);

        verify(itemRepository).delete(testItem);
    }

    @Test
    void deleteItem_ShouldThrowException_WhenHasStock() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(stockRepository.findQuantityByItemId(1L)).thenReturn(Optional.of(10));
        when(stockRepository.sumQuantityByItemIdThroughVariants(1L)).thenReturn(0);

        assertThrows(DeleteConstraintException.class, () -> itemService.deleteItem(1L));
        verify(itemRepository, never()).delete(any());
    }
}
