package com.dacia1704.truyenonline.module.interaction.dto.response;

import com.dacia1704.truyenonline.module.user.dto.response.UserResponse;
import jakarta.persistence.*;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    String id;
    String type;
    String content;
    UserResponse author;
    List<CommentResponse> replies;
}
