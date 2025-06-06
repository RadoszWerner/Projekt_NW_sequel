package com.example.backend.mapper;

import com.example.backend.dto.PostDTO;
import com.example.backend.dto.UserDTO;
import com.example.backend.model.Post;

public class PostMapper {

    public static PostDTO mapToPostDTO(Post post) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(post.getUser().getId());
        userDTO.setUsername(post.getUser().getUsername());
        userDTO.setIsModerator(post.getUser().getIsModerator());

        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getId());
        postDTO.setTitle(post.getTitle());
        postDTO.setToxic(post.getIsToxic());
        postDTO.setDeleted(post.getIsDeleted());
        postDTO.setContent(post.getContent());
        postDTO.setCreatedAt(post.getCreatedAt().toString());
        postDTO.setUpdatedAt(post.getUpdatedAt() != null ? post.getUpdatedAt().toString() : null);
        postDTO.setUser(userDTO);

        return postDTO;
    }
}
