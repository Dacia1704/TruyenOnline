package com.dacia1704.truyenonline.module.banner.repository;

import com.dacia1704.truyenonline.module.banner.entity.Banner;
import com.dacia1704.truyenonline.module.banner.entity.BannerPosition;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerRepository extends JpaRepository<Banner, String> {

    List<Banner> findByIsActiveTrueOrderBySortOrderAsc();

    List<Banner> findByPositionAndIsActiveTrueOrderBySortOrderAsc(BannerPosition position);

    Page<Banner> findAllByIsActiveOrderBySortOrderAsc(boolean isActive, Pageable pageable);
}
