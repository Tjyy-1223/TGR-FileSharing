package com.tjyy.sharing.service.user.respository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tjyy.sharing.service.user.respository.entity.UserDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户登录 mapper 接口
 * 用于 UserDO 和 数据库表 - user 进行连接
 */
public interface UserMapper extends BaseMapper<UserDO> {
    /**
     * 根据第三方 id 进行查询
     * @param accountId
     * @return
     */
    @Select("select * from user where third_account_id = #{account_id} limit 1")
    UserDO getByThirdAccountId(@Param("account_id") String accountId);


    /**
     * 遍历用户id
     * @param offsetUserId
     * @param limitUserId
     * @return
     */
    @Select("select id from user where id > #{offsetUserId} order by id asc limit #{size};")
    List<Long> getUserIdsOrderByIdAsc(@Param("offsetUserId") Long offsetUserId, @Param("size") Long limitUserId);
}
