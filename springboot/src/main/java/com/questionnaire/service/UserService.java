package com.questionnaire.service;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.questionnaire.common.Constants;
import com.questionnaire.common.enums.ResultCodeEnum;
import com.questionnaire.common.enums.RoleEnum;

import com.questionnaire.entity.Account;
import com.questionnaire.entity.Admin;
import com.questionnaire.entity.User;
import com.questionnaire.exception.CustomException;
import com.questionnaire.mapper.UserMapper;
import com.questionnaire.utils.TokenUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户业务处理
 **/
@Service
public class UserService {

    @Resource
    UserMapper userMapper;

    /**
     * 查询所有用户
     * @param user
     * @return
     */
    public List<User> selectAll(User user) {
        return userMapper.selectAll(user);
    }

    /**
     * 添加用户
     * @param user
     */
    public void add(User user) {
        User dbUser = userMapper.selectByUsername(user.getUsername());
            if (ObjectUtil.isNotNull(dbUser)) {
                throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
            }
            if (ObjectUtil.isEmpty(user.getPassword())) {
                user.setPassword(Constants.USER_DEFAULT_PASSWORD);  // 设置默认密码
            }
            //判断用户名是否为空
            if (ObjectUtil.isEmpty(user.getName())) {
                user.setName(user.getUsername());
            }

            //添加用户角色
            user.setRole(RoleEnum.USER.name());
            userMapper.insert(user);
    }

    /**
     * 根据id删除
     * @param id
     */
    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    /**
     * 批量删除
     * @param ids
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    public User selectById(Integer id) {
        return userMapper.selectById(id);
    }

    public PageInfo<User> selectPage(User user, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.selectAll(user);
        return PageInfo.of(userList);
    }

    /**
     * 根据id更新用户
     * @param user
     */
    public void updateById(User user) {
        userMapper.updateById(user);
    }

    /**
     * 用户登录
     * @param account
     * @return
     */
    public Account login(Account account) {
        Account dbUser = userMapper.selectByUsername(account.getUsername());
        //判断用户输入
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbUser.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String tokenData = dbUser.getId() + "-" + RoleEnum.USER.name();
        String token = TokenUtils.createToken(tokenData, dbUser.getPassword());
        dbUser.setToken(token);


        return dbUser;
    }

    /**
     * 用户注册
     * @param account
     */
    public void register(Account account) {
        User user = new User();
        BeanUtils.copyProperties(account, user);
        add(user);
    }

    /**
     * 修改密码
     * @param account
     */
    public void updatePassword(Account account) {
        User dbUser = userMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbUser.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbUser.setPassword(account.getNewPassword());
        userMapper.updateById(dbUser);
    }
}