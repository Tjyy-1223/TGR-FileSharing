package com.tjyy.sharing.service.user.respository.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tjyy.sharing.api.enums.YesOrNoEnum;
import com.tjyy.sharing.service.user.respository.entity.UserInfoDO;
import com.tjyy.sharing.service.user.respository.mapper.UserInfoMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author: Tjyy
 * @date: 2024-06-14 00:02
 * @description: 用户详情 - Dao 层函数
 */
@Repository
public class UserInfoDao extends ServiceImpl<UserInfoMapper, UserInfoDO> {

    /**
     * 根据 username 进行姓名相似的读取
     * @param userName 用户名
     * @return
     */
    public List<UserInfoDO> getByUsernameLike(String userName) {
        LambdaQueryWrapper<UserInfoDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.select(UserInfoDO::getUserId, UserInfoDO::getUsername, UserInfoDO::getPhoto, UserInfoDO::getProfile)
                .and(!StringUtils.isEmpty(userName), v -> v.like(UserInfoDO::getUsername, userName))
                .eq(UserInfoDO::getDeleted, YesOrNoEnum.NO.getCode());
        return baseMapper.selectList(queryWrapper);
    }


    /**
     * 根据用户 id 来寻找用户，注意不能读取已经删除的用户
     * @param userId 用户-id
     * @return
     */
    public UserInfoDO queryByUserId(String userId) {
        LambdaQueryWrapper<UserInfoDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(UserInfoDO::getUserId, userId)
                .eq(UserInfoDO::getDeleted, YesOrNoEnum.NO.getCode());
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 根据用户 id 来获取 - UserInfoDO 列表
     * @param userIds
     * @return
     */
    public List<UserInfoDO> getByUserIds(Collection<Integer> userIds){
        LambdaQueryWrapper<UserInfoDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(UserInfoDO::getUserId, userIds)
                .eq(UserInfoDO::getDeleted, YesOrNoEnum.NO.getCode());
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 获取用户数量
     * @return
     */
    public Long getUserCount(){
        return lambdaQuery()
                .eq(UserInfoDO::getDeleted, YesOrNoEnum.NO.getCode())
                .count();
    }

    /**
     * 更新对应的用户信息
     * @param user
     */
    public void updateUserInfo(UserInfoDO user){
        UserInfoDO record = baseMapper.selectById(user.getUserId());
        if (record.equals(user)) {
            return;
        }
        if (StringUtils.isEmpty(user.getPhoto())) {
            user.setPhoto(null);
        }
        if (StringUtils.isEmpty(user.getUsername())) {
            user.setUsername(null);
        }

        user.setId(record.getId());
        updateById(user);
    }
}
