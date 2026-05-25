package com.lovers.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovers.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("""
        SELECT id, username, password, nickname, avatar, avatar_content_type, avatar_data, gender, signature,
               create_time, update_time, is_deleted
        FROM user
        WHERE id = #{userId} AND is_deleted = 0
        """)
    User selectAvatarContentById(@Param("userId") Long userId);
}
