package com.dacia1704.truyenonline.module.banner.mapper;

import com.dacia1704.truyenonline.module.banner.dto.request.BannerCreateRequest;
import com.dacia1704.truyenonline.module.banner.dto.request.BannerUpdateRequest;
import com.dacia1704.truyenonline.module.banner.dto.response.BannerResponse;
import com.dacia1704.truyenonline.module.banner.entity.Banner;
import com.dacia1704.truyenonline.module.user.mapper.UserMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        uses = {
                UserMapper.class
        }
)
public interface BannerMapper {

    Banner toBanner(BannerCreateRequest request);

    BannerResponse toBannerResponse(Banner banner);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBanner(
            @MappingTarget Banner banner,
            BannerUpdateRequest request
    );
}