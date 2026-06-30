package com.dacia1704.truyenonline.module.user.repository;

import com.dacia1704.truyenonline.module.user.entity.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    List<Role> findByIsDefaultTrue();

    Optional<Role> findByName(String name);
}
