package com.dacia1704.truyenonline.shared.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {
    int currentPage;
    int pageSize;
    int totalPages;
    long totalElements;
    List<T> data;
}