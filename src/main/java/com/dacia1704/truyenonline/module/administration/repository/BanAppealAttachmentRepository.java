package com.dacia1704.truyenonline.module.administration.repository;

import com.dacia1704.truyenonline.module.administration.entity.BanAppealAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BanAppealAttachmentRepository extends JpaRepository<BanAppealAttachment, String> {

    List<BanAppealAttachment> findAllByAppeal_Id(String appealId);

    void deleteAllByAppeal_Id(String appealId);
}