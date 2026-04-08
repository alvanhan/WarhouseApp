package com.warehouse.api.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginatedResponse<T> {

    private String status;
    private String message;
    private List<T> data;
    private Meta meta;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        private int currentPage;
        private int perPage;
        private long totalItems;
        private int totalPages;
        private String sort;
        private String order;
        private Map<String, Object> filters;
    }

    public static <T> PaginatedResponse<T> of(
            List<T> data,
            int currentPage,
            int perPage,
            long totalItems,
            String sort,
            String order,
            Map<String, Object> filters
    ) {
        int totalPages = (int) Math.ceil((double) totalItems / perPage);
        return PaginatedResponse.<T>builder()
                .status("success")
                .message("Data fetched successfully")
                .data(data)
                .meta(Meta.builder()
                        .currentPage(currentPage)
                        .perPage(perPage)
                        .totalItems(totalItems)
                        .totalPages(totalPages)
                        .sort(sort)
                        .order(order)
                        .filters(filters)
                        .build())
                .build();
    }
}
