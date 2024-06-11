package com.tjyy.sharing.service.user.respository.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tjyy.sharing.api.enums.YesOrNoEnum;
import com.tjyy.sharing.api.vo.PageParam;
import com.tjyy.sharing.service.user.respository.entity.UserDO;
import com.tjyy.sharing.service.user.respository.mapper.UserMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.print.attribute.standard.PageRanges;
import java.util.List;

/**
 * @author: Tjyy
 * @date: 2024-06-12 21:09
 * @description: UserDao - 根据 UserMapper 进行 UserDO 的功能组装
 */
@Repository
public class UserDao extends ServiceImpl<UserMapper, UserDO> {

    /**
     * 根据传入的 userId 和 size 来获取区间内所有存在的 userId
     * @param userId
     * @param size
     * @return
     */
    public List<Long> scanUserId(Long userId, Long size) {
        return baseMapper.getUserIdsOrderByIdAsc(userId, size == null ? PageParam.DEFAULT_PAGE_SIZE : size);
    }

    /**
     * 第三方账号进行登录，返回用户信息
     * @param accountId
     * @return
     */
    public UserDO getByThirdAccountId(String accountId) {
        return baseMapper.getByThirdAccountId(accountId);
    }


    /**
     * 根据 UserDO 中是否有 userId，插入或者更新 userDO
     * @param userDO
     */
    public void saveUser(UserDO userDO) {
        if (userDO.getId() == null){
            baseMapper.insert(userDO);
        }else{
            baseMapper.updateById(userDO);
        }
    }

    /**
     * 根据传入的用户名返回对应的 UserDO
     * @param username
     * @return
     */
    public UserDO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(UserDO::getUsername, username)
                .eq(UserDO::getDeleted, YesOrNoEnum.NO.getCode())
                .last("limit 1");
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 根据 UserId 获取对应的 User 类型
     * @param id
     * @return
     */
    public UserDO getUserById(Long id) {
        return baseMapper.selectById(id);
    }

    /**
     * 根据 UserId 跟新对应的 User
     * @param userDO
     */
    public void updateUser(UserDO userDO) {
        baseMapper.updateById(userDO);
    }

}
