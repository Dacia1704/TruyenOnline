package com.dacia1704.truyenonline.module.interaction.mapper;

import com.dacia1704.truyenonline.module.interaction.dto.response.CommentResponse;
import com.dacia1704.truyenonline.module.interaction.entity.Comment;
import com.dacia1704.truyenonline.module.user.mapper.UserMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class})
public interface CommentMapper {

    @Mapping(source = "user", target = "author")
    CommentResponse toCommentResponse(Comment comment);
}
