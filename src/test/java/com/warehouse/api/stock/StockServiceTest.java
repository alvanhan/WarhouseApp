package com.warehouse.api.stock;

import com.warehouse.api.common.exception.InsufficientStockException;
import com.warehouse.api.common.exception.ResourceNotFoundException;
import com.warehouse.api.item.entity.Item;
import com.warehouse.api.item.repository.ItemRepository;
import com.warehouse.api.stock.dto.SellRequest;
import com.warehouse.api.stock.dto.SellResponse;
import com.warehouse.api.stock.dto.StockRequest;
import com.warehouse.api.stock.dto.StockResponse;
import com.warehouse.api.stock.entity.Stock;
import com.warehouse.api.stock.mapper.StockMapper;
import com.warehouse.api.stock.repository.StockRepository;
import com.warehouse.api.stock.service.StockServiceImpl;
import com.warehouse.api.variant.entity.Variant;
import com.warehouse.api.variant.repository.VariantRepository;
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
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private VariantRepository variantRepository;

    @Mock
    private StockMapper stockMapper;

    @InjectMocks
    private StockServiceImpl stockService;

    private Item testItem;
    private Variant testVariant;
    private Stock testStock;

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

        testStock = Stock.builder()
                .id(1L)
                .variant(testVariant)
                .quantity(10)
                .build();
    }

    @Test
    void updateItemStock_ShouldCreateNewStock_WhenNotExists() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(stockRepository.findByItemId(1L)).thenReturn(Optional.empty());
        when(stockRepository.save(any(Stock.class))).thenAnswer(inv -> {
            Stock s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        StockRequest request = StockRequest.builder().quantity(50).build();
        StockResponse result = stockService.updateItemStock(1L, request);

        assertNotNull(result);
        assertEquals(50, result.getQuantity());
        verify(stockRepository).save(any(Stock.class));
    }

    @Test
    void updateItemStock_ShouldUpdateExistingStock() {
        Stock existingStock = Stock.builder()
                .id(1L)
                .item(testItem)
                .quantity(20)
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(stockRepository.findByItemId(1L)).thenReturn(Optional.of(existingStock));
        when(stockRepository.save(any(Stock.class))).thenReturn(existingStock);

        StockRequest request = StockRequest.builder().quantity(50).build();
        StockResponse result = stockService.updateItemStock(1L, request);

        assertNotNull(result);
        assertEquals(50, result.getQuantity());
    }

    @Test
    void updateVariantStock_ShouldReturnUpdatedStock() {
        when(variantRepository.findById(1L)).thenReturn(Optional.of(testVariant));
        when(stockRepository.findByVariantId(1L)).thenReturn(Optional.of(testStock));
        when(stockRepository.save(any(Stock.class))).thenReturn(testStock);

        StockRequest request = StockRequest.builder().quantity(30).build();
        StockResponse result = stockService.updateVariantStock(1L, request);

        assertNotNull(result);
        assertEquals(30, result.getQuantity());
    }

    @Test
    void sell_ShouldDeductStock_WhenSufficientQuantity() {
        when(stockRepository.findByVariantIdWithLock(1L)).thenReturn(Optional.of(testStock));
        when(stockRepository.save(any(Stock.class))).thenReturn(testStock);

        SellRequest request = SellRequest.builder()
                .variantId(1L)
                .quantity(3)
                .build();

        SellResponse result = stockService.sell(request);

        assertNotNull(result);
        assertEquals(7, result.getRemaining());
    }

    @Test
    void sell_ShouldThrowException_WhenInsufficientStock() {
        when(stockRepository.findByVariantIdWithLock(1L)).thenReturn(Optional.of(testStock));

        SellRequest request = SellRequest.builder()
                .variantId(1L)
                .quantity(15)
                .build();

        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> stockService.sell(request)
        );

        assertEquals(10, exception.getAvailable());
        assertEquals(15, exception.getRequested());
    }

    @Test
    void sell_ShouldThrowException_WhenStockNotFound() {
        when(stockRepository.findByVariantIdWithLock(1L)).thenReturn(Optional.empty());

        SellRequest request = SellRequest.builder()
                .variantId(1L)
                .quantity(5)
                .build();

        assertThrows(ResourceNotFoundException.class, () -> stockService.sell(request));
    }

    @Test
    void sell_ShouldThrowException_WhenNoIdProvided() {
        SellRequest request = SellRequest.builder()
                .quantity(5)
                .build();

        assertThrows(IllegalArgumentException.class, () -> stockService.sell(request));
    }

    @Test
    void sell_ShouldWorkWithItemId_WhenNoVariantId() {
        Stock itemStock = Stock.builder()
                .id(2L)
                .item(testItem)
                .quantity(20)
                .build();

        when(stockRepository.findByItemIdWithLock(1L)).thenReturn(Optional.of(itemStock));
        when(stockRepository.save(any(Stock.class))).thenReturn(itemStock);

        SellRequest request = SellRequest.builder()
                .itemId(1L)
                .quantity(5)
                .build();

        SellResponse result = stockService.sell(request);

        assertNotNull(result);
        assertEquals(15, result.getRemaining());
    }

    @Test
    void sell_ShouldReduceToZero_WhenSellingAllStock() {
        testStock.setQuantity(5);
        when(stockRepository.findByVariantIdWithLock(1L)).thenReturn(Optional.of(testStock));
        when(stockRepository.save(any(Stock.class))).thenReturn(testStock);

        SellRequest request = SellRequest.builder()
                .variantId(1L)
                .quantity(5)
                .build();

        SellResponse result = stockService.sell(request);

        assertEquals(0, result.getRemaining());
    }
}
