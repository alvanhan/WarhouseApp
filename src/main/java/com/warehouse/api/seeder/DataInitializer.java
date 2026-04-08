package com.warehouse.api.seeder;

import com.warehouse.api.item.entity.Item;
import com.warehouse.api.item.repository.ItemRepository;
import com.warehouse.api.stock.entity.Stock;
import com.warehouse.api.stock.repository.StockRepository;
import com.warehouse.api.variant.entity.Variant;
import com.warehouse.api.variant.repository.VariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ItemRepository itemRepository;
    private final VariantRepository variantRepository;
    private final StockRepository stockRepository;

    @Override
    public void run(String... args) {
        if (itemRepository.count() > 0) {
            return;
        }

        Item kaosPolos = itemRepository.save(Item.builder()
                .name("Kaos Polos")
                .description("Kaos cotton combed 30s")
                .basePrice(new BigDecimal("75000"))
                .build());

        Variant kaosWhiteM = variantRepository.save(Variant.builder()
                .item(kaosPolos)
                .name("White - M")
                .sku("KP-WHT-M")
                .price(new BigDecimal("75000"))
                .build());

        Variant kaosWhiteL = variantRepository.save(Variant.builder()
                .item(kaosPolos)
                .name("White - L")
                .sku("KP-WHT-L")
                .price(new BigDecimal("80000"))
                .build());

        Variant kaosRedM = variantRepository.save(Variant.builder()
                .item(kaosPolos)
                .name("Red - M")
                .sku("KP-RED-M")
                .price(new BigDecimal("75000"))
                .build());

        Variant kaosRedXL = variantRepository.save(Variant.builder()
                .item(kaosPolos)
                .name("Red - XL")
                .sku("KP-RED-XL")
                .price(new BigDecimal("85000"))
                .build());

        stockRepository.save(Stock.builder().variant(kaosWhiteM).quantity(10).build());
        stockRepository.save(Stock.builder().variant(kaosWhiteL).quantity(8).build());
        stockRepository.save(Stock.builder().variant(kaosRedM).quantity(3).build());
        stockRepository.save(Stock.builder().variant(kaosRedXL).quantity(0).build());

        Item celanaJeans = itemRepository.save(Item.builder()
                .name("Celana Jeans")
                .description("Celana jeans stretch premium")
                .basePrice(new BigDecimal("250000"))
                .build());

        Variant jeansBlue32 = variantRepository.save(Variant.builder()
                .item(celanaJeans)
                .name("Blue - 32")
                .sku("CJ-BLU-32")
                .price(new BigDecimal("250000"))
                .build());

        Variant jeansBlack34 = variantRepository.save(Variant.builder()
                .item(celanaJeans)
                .name("Black - 34")
                .sku("CJ-BLK-34")
                .price(new BigDecimal("275000"))
                .build());

        stockRepository.save(Stock.builder().variant(jeansBlue32).quantity(15).build());
        stockRepository.save(Stock.builder().variant(jeansBlack34).quantity(5).build());

        Item topiBaseball = itemRepository.save(Item.builder()
                .name("Topi Baseball")
                .description("Topi baseball cotton dengan logo bordir")
                .basePrice(new BigDecimal("45000"))
                .build());

        stockRepository.save(Stock.builder().item(topiBaseball).quantity(25).build());

        Item tasRansel = itemRepository.save(Item.builder()
                .name("Tas Ransel")
                .description("Tas ransel waterproof kapasitas 25L")
                .basePrice(new BigDecimal("350000"))
                .build());

        Variant tasHitam = variantRepository.save(Variant.builder()
                .item(tasRansel)
                .name("Hitam")
                .sku("TR-HTM")
                .price(new BigDecimal("350000"))
                .build());

        Variant tasNavy = variantRepository.save(Variant.builder()
                .item(tasRansel)
                .name("Navy")
                .sku("TR-NVY")
                .price(new BigDecimal("350000"))
                .build());

        stockRepository.save(Stock.builder().variant(tasHitam).quantity(12).build());
        stockRepository.save(Stock.builder().variant(tasNavy).quantity(7).build());
    }
}
