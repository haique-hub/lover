package com.lovers.controller;

import com.lovers.common.Result;
import com.lovers.context.UserContext;
import com.lovers.dto.CommentCreateDTO;
import com.lovers.service.CommentService;
import com.lovers.vo.CommentVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public Result<CommentVO> create(@Valid @RequestBody CommentCreateDTO dto) {
        return Result.success(commentService.createComment(UserContext.getUserId(), dto));
    }

    @DeleteMapping("/{commentId}")
    public Result<Void> delete(@PathVariable Long commentId) {
        commentService.deleteComment(UserContext.getUserId(), commentId);
        return Result.success();
    }

    @GetMapping("/photo/{photoId}")
    public Result<List<CommentVO>> list(@PathVariable Long photoId) {
        return Result.success(commentService.listPhotoComments(UserContext.getUserId(), photoId));
    }
}
