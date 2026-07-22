package com.dacia1704.truyenonline.module.interaction.dto.request;

import com.dacia1704.truyenonline.module.interaction.entity.HistoryType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

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
