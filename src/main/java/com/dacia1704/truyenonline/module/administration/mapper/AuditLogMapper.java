package com.dacia1704.truyenonline.module.administration.mapper;

import com.dacia1704.truyenonline.module.administration.dto.request.AuditLogCreateRequest;
import com.dacia1704.truyenonline.module.administration.dto.response.AuditLogResponse;
import com.dacia1704.truyenonline.module.administration.entity.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    @Mapping(target = "actorId", source = "actor.id")
    @Mapping(target = "actorUsername", source = "actor.username")
    AuditLogResponse toResponse(AuditLog entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actor", ignore = true)
    @Mapping(target = "actorRole", ignore = true)
    AuditLog toAuditLog(AuditLogCreateRequest request);
}