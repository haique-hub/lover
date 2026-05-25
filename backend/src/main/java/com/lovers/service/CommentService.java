package com.lovers.service;

import com.lovers.dto.CommentCreateDTO;
import com.lovers.vo.CommentVO;
import java.util.List;

public interface CommentService {

    CommentVO createComment(Long userId, CommentCreateDTO dto);

    void deleteComment(Long userId, Long commentId);

    List<CommentVO> listPhotoComments(Long userId, Long photoId);
}
