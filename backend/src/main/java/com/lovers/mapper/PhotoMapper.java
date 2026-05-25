package com.lovers.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lovers.entity.Photo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PhotoMapper extends BaseMapper<Photo> {

    @Select("""
        SELECT id, album_id, user_id, file_name, file_url, content_type, description, sort_order, file_data,
               create_time, update_time, is_deleted
        FROM photo
        WHERE id = #{photoId} AND is_deleted = 0
        """)
    Photo selectContentById(@Param("photoId") Long photoId);
}
