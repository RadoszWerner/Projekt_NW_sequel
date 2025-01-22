package com.example.backend.mapper;

import com.example.backend.dto.CommentDTO;
import com.example.backend.dto.UserDTO;
import com.example.backend.model.Comment;

public class CommentMapper {

    public static CommentDTO mapToCommentDTO(Comment comment) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(comment.getUser().getId());
        userDTO.setUsername(comment.getUser().getUsername());
        userDTO.setIsModerator(comment.getUser().getIsModerator());

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setCreatedAt(comment.getCreatedAt().toString());
        commentDTO.setDeleted(comment.getIsDeleted());
        commentDTO.setToxic(comment.getIsToxic());
        commentDTO.setUser(userDTO);

        return commentDTO;
    }

}
