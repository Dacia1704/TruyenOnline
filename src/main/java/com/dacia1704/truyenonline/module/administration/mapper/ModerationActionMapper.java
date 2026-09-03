package com.dacia1704.truyenonline.module.administration.mapper;

import com.dacia1704.truyenonline.module.administration.dto.request.ModerationActionCreateRequest;
import com.dacia1704.truyenonline.module.administration.dto.response.ModerationActionResponse;
import com.dacia1704.truyenonline.module.administration.entity.ModerationAction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ModerationActionMapper {

    @Mapping(target = "adminId", source = "admin.id")
    @Mapping(target = "adminUsername", source = "admin.username")
    ModerationActionResponse toResponse(ModerationAction entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "admin", ignore = true)
    ModerationAction toModerationAction(ModerationActionCreateRequest request);
}
