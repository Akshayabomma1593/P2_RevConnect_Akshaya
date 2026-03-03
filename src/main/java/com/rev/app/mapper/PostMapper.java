package com.rev.app.mapper;

import com.rev.app.dto.PostDTO;
import com.rev.app.entity.Post;
import com.rev.app.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public Post toEntity(PostDTO dto, User author) {
        Post post = new Post();
        post.setAuthor(author);
        post.setContent(dto.getContent() != null ? dto.getContent() : "");
        String tags = dto.getHashtags();
        if (tags != null) {
            // Normalize: replace spaces with commas, then clean up double commas
            tags = tags.replace(" ", ",").replaceAll(",+", ",");
        }
        post.setHashtags(tags);
        String productTags = dto.getProductTags();
        if (productTags != null) {
            productTags = productTags.replace(";", ",").replaceAll(",+", ",");
        }
        post.setProductTags(productTags);
        post.setPostType(dto.getPostType() != null ? dto.getPostType() : Post.PostType.REGULAR);
        post.setCtaLabel(dto.getCtaLabel());
        post.setCtaUrl(dto.getCtaUrl());
        post.setScheduledAt(dto.getScheduledAt());
        post.setPinned(dto.isPinned());
        post.setPublished(dto.getScheduledAt() == null);
        return post;
    }
}
