package com.dacia1704.truyenonline.module.interaction.dto.request;

import java.time.LocalDate;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReadingHistoryFilter {
    String sessionId;

    ReadingHistoryFilterType type;

    LocalDate fromDate;

    LocalDate toDate;
}
