package com.dacia1704.truyenonline.module.user.repository;

import com.dacia1704.truyenonline.module.user.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {}
