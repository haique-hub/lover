package com.lovers.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lovers.dto.CommentCreateDTO;
import com.lovers.entity.Photo;
import com.lovers.entity.PhotoComment;
import com.lovers.entity.User;
import com.lovers.exception.BusinessException;
import com.lovers.mapper.PhotoCommentMapper;
import com.lovers.service.CommentService;
import com.lovers.service.PhotoService;
import com.lovers.service.UserService;
import com.lovers.vo.CommentVO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final PhotoCommentMapper photoCommentMapper;
    private final PhotoService photoService;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentVO createComment(Long userId, CommentCreateDTO dto) {
        photoService.verifyPhotoAccessible(userId, dto.getPhotoId());
        Long parentId = dto.getParentId() == null ? 0L : dto.getParentId();
        PhotoComment parentComment = null;
        Long replyUserId = dto.getReplyUserId();
        if (parentId > 0) {
            parentComment = photoCommentMapper.selectById(parentId);
            if (parentComment == null || !Objects.equals(parentComment.getPhotoId(), dto.getPhotoId())) {
                throw new BusinessException("父评论不存在");
            }
            if (replyUserId == null) {
                replyUserId = parentComment.getUserId();
            }
        }
        PhotoComment comment = new PhotoComment();
        comment.setPhotoId(dto.getPhotoId());
        comment.setUserId(userId);
        comment.setReplyUserId(replyUserId);
        comment.setParentId(parentId);
        comment.setContent(dto.getContent());
        photoCommentMapper.insert(comment);
        Set<Long> userIds = Stream.of(userId, replyUserId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        Map<Long, User> userMap = userService.getUserMap(userIds);
        return toCommentVO(comment, userMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long userId, Long commentId) {
        PhotoComment comment = photoCommentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        Photo photo = photoService.getByIdOrThrow(comment.getPhotoId());
        if (!comment.getUserId().equals(userId) && !photo.getUserId().equals(userId)) {
            throw new BusinessException("只有评论作者或照片上传者可以删除评论");
        }
        List<PhotoComment> allPhotoComments = photoCommentMapper.selectList(
            Wrappers.<PhotoComment>lambdaQuery().eq(PhotoComment::getPhotoId, comment.getPhotoId())
        );
        List<Long> deleteIds = new ArrayList<>();
        collectDeleteIds(commentId, allPhotoComments, deleteIds);
        photoCommentMapper.delete(Wrappers.<PhotoComment>lambdaQuery().in(PhotoComment::getId, deleteIds));
    }

    @Override
    public List<CommentVO> listPhotoComments(Long userId, Long photoId) {
        photoService.verifyPhotoAccessible(userId, photoId);
        List<PhotoComment> comments = photoCommentMapper.selectList(
            Wrappers.<PhotoComment>lambdaQuery()
                .eq(PhotoComment::getPhotoId, photoId)
                .orderByAsc(PhotoComment::getCreateTime)
        );
        if (comments.isEmpty()) {
            return List.of();
        }
        Set<Long> userIds = comments.stream()
            .flatMap(comment -> Stream.of(comment.getUserId(), comment.getReplyUserId()))
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        Map<Long, User> userMap = userService.getUserMap(userIds);
        Map<Long, CommentVO> commentVOMap = comments.stream()
            .map(comment -> toCommentVO(comment, userMap))
            .collect(Collectors.toMap(CommentVO::getId, comment -> comment, (left, right) -> left, LinkedHashMap::new));
        List<CommentVO> roots = new ArrayList<>();
        for (PhotoComment comment : comments) {
            CommentVO current = commentVOMap.get(comment.getId());
            if (comment.getParentId() == null || comment.getParentId() == 0) {
                roots.add(current);
                continue;
            }
            CommentVO parent = commentVOMap.get(comment.getParentId());
            if (parent == null) {
                roots.add(current);
            } else {
                parent.getChildren().add(current);
            }
        }
        return roots;
    }

    private void collectDeleteIds(Long parentId, Collection<PhotoComment> comments, List<Long> deleteIds) {
        deleteIds.add(parentId);
        for (PhotoComment comment : comments) {
            if (Objects.equals(comment.getParentId(), parentId)) {
                collectDeleteIds(comment.getId(), comments, deleteIds);
            }
        }
    }

    private CommentVO toCommentVO(PhotoComment comment, Map<Long, User> userMap) {
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(comment, vo);
        User user = userMap.get(comment.getUserId());
        if (user != null) {
            vo.setUserNickname(user.getNickname());
            vo.setUserAvatar(user.getAvatar());
        }
        User replyUser = userMap.get(comment.getReplyUserId());
        if (replyUser != null) {
            vo.setReplyUserNickname(replyUser.getNickname());
        }
        return vo;
    }
}
