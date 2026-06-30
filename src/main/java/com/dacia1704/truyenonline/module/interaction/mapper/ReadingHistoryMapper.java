package com.dacia1704.truyenonline.module.interaction.mapper;

import com.dacia1704.truyenonline.module.interaction.dto.response.ReadingHistoryResponse;
import com.dacia1704.truyenonline.module.interaction.entity.ReadingHistory;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReadingHistoryMapper {
    ReadingHistoryResponse toReadingHistoryResponse(ReadingHistory readingHistory);
}
