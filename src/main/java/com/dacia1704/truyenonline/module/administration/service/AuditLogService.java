package com.dacia1704.truyenonline.module.administration.service;

import com.dacia1704.truyenonline.module.administration.dto.request.AuditLogCreateRequest;
import com.dacia1704.truyenonline.module.administration.dto.request.AuditLogPageRequest;
import com.dacia1704.truyenonline.module.administration.dto.request.ModerationActionCreateRequest;
import com.dacia1704.truyenonline.module.administration.dto.request.ModerationActionPageRequest;
import com.dacia1704.truyenonline.module.administration.dto.response.AuditLogResponse;
import com.dacia1704.truyenonline.module.administration.dto.response.ModerationActionResponse;
import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditLog;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationAction;
import com.dacia1704.truyenonline.module.administration.mapper.AuditLogMapper;
import com.dacia1704.truyenonline.module.administration.mapper.ModerationActionMapper;
import com.dacia1704.truyenonline.module.administration.repository.AuditLogRepository;
import com.dacia1704.truyenonline.module.administration.repository.ModerationActionRepository;
import com.dacia1704.truyenonline.module.administration.repository.specification.AuditLogSpecification;
import com.dacia1704.truyenonline.module.administration.repository.specification.ModerationActionSpecification;
import com.dacia1704.truyenonline.module.user.entity.Role;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.module.user.repository.UserRepository;
import com.dacia1704.truyenonline.module.user.service.UserService;
import com.dacia1704.truyenonline.shared.exception.AppException;
import com.dacia1704.truyenonline.shared.exception.ErrorCode;
import com.dacia1704.truyenonline.shared.response.PageResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class AuditLogService {
    AuditLogMapper auditLogMapper;
    AuditLogRepository auditLogRepository;
    UserRepository userRepository;
    ObjectMapper objectMapper;

    public PageResponse<AuditLogResponse> getAuditLogs(
            AuditLogPageRequest request) {

        int pageNo = request.getPage() > 0 ? request.getPage() - 1 : 0;

        Pageable pageable = PageRequest.of(
                pageNo,
                request.getSize(),
                Sort.by("createdAt").descending()
        );

        Specification<AuditLog> spec =
                AuditLogSpecification.filterAuditLogs(
                        request.getActorId(),
                        request.getAction(),
                        request.getObjectType(),
                        request.getObjectId(),
                        request.getFromDate(),
                        request.getToDate()
                );

        Page<AuditLog> auditLogPage =
                auditLogRepository.findAll(spec, pageable);

        List<AuditLogResponse> responses =
                auditLogPage.getContent()
                        .stream()
                        .map(auditLogMapper::toResponse)
                        .toList();

        return PageResponse.<AuditLogResponse>builder()
                .currentPage(request.getPage())
                .pageSize(auditLogPage.getSize())
                .totalPages(auditLogPage.getTotalPages())
                .totalElements(auditLogPage.getTotalElements())
                .data(responses)
                .build();
    }

    public AuditLogResponse createAuditLog(
            AuditLogCreateRequest request) {

        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        User currentUser =  userRepository
                .findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        AuditLog auditLog = auditLogMapper.toAuditLog(request);

        auditLog.setActor(currentUser);
        String roles = currentUser.getRoles()
                .stream()
                .map(Role::getName)
                .sorted()
                .collect(Collectors.joining(","));

        auditLog.setActorRole(roles);

        auditLog = auditLogRepository.save(auditLog);

        return auditLogMapper.toResponse(auditLog);
    }

    public void log(
            AuditAction action,
            AuditObjectType objectType,
            String objectId,
            Object oldValue,
            Object newValue,
            String description
    ) {
        try {
            createAuditLog(
                    AuditLogCreateRequest.builder()
                            .action(action)
                            .objectType(objectType)
                            .objectId(objectId)
                            .description(description)
                            .oldValue(oldValue == null ? null : objectMapper.writeValueAsString(oldValue))
                            .newValue(newValue == null ? null : objectMapper.writeValueAsString(newValue))
                            .build()
            );
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
