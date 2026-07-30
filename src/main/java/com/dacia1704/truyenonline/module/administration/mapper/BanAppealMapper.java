package com.dacia1704.truyenonline.module.administration.mapper;

import com.dacia1704.truyenonline.module.administration.dto.request.BanAppealCreateRequest;
import com.dacia1704.truyenonline.module.administration.dto.request.BanAppealResponse;
import com.dacia1704.truyenonline.module.administration.dto.response.BanAppealAttachmentResponse;
import com.dacia1704.truyenonline.module.administration.entity.BanAppeal;
import com.dacia1704.truyenonline.module.administration.entity.BanAppealAttachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BanAppealMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "moderationAction", ignore = true)
    @Mapping(target = "reviewer", ignore = true)
    @Mapping(target = "reviewerNote", ignore = true)
    @Mapping(target = "resolvedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BanAppeal toBanAppeal(BanAppealCreateRequest request);

    BanAppealResponse toBanAppealResponse(BanAppeal appeal);

    List<BanAppealResponse> toBanAppealResponses(List<BanAppeal> appeals);

    BanAppealAttachmentResponse toAttachmentResponse(BanAppealAttachment attachment);

    List<BanAppealAttachmentResponse> toAttachmentResponses(List<BanAppealAttachment> attachments);
}