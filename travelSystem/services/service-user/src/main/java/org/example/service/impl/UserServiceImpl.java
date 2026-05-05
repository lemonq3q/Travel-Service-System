package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import com.github.pagehelper.PageHelper;
import org.example.domain.encapsulate.TableData;
import org.example.domain.authenticate.UserRole;
import org.example.domain.encapsulate.ResponseResult;
import org.example.domain.user.User;
import org.example.domain.user.UserSelectDTO;
import org.example.mapper.RoleMapper;
import org.example.mapper.UserMapper;
import org.example.mapper.UserRoleMapper;
import org.example.service.UserService;
import org.example.utils.SystemCommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    private final String DEFAULT_PASSWORD = "qwer1234";


    @Override
    public ResponseResult<User> selectByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        wrapper.eq(User::getIsDelete, 0);
        User user = userMapper.selectOne(wrapper);
        if(user == null){
            return new ResponseResult<>(404, "用户不存在");
        }
        else{
            return new ResponseResult<>(200, user);
        }
    }


    @Override
    public ResponseResult updatePassword(User params) {
        Long userid = params.getId();
        if(userid == null && params.getEmail() == null){
            return new ResponseResult(400, "更新失败，用户不存在");
        }
        String password = params.getPassword();
        if (password == null || password.isEmpty() || password.length() < 6 || password.length() > 16){
            return new ResponseResult(400, "更新失败，违规密码");
        }
        password = passwordEncoder.encode(password);
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        if(userid != null){
            wrapper.eq(User::getId, userid);
        }
        if(params.getEmail() != null){
            wrapper.eq(User::getEmail, params.getEmail());
        }
        wrapper.eq(User::getIsDelete, 0);
        wrapper.set(User::getPassword, password);
        int x = userMapper.update(null, wrapper);
        if (x > 0){
            return new ResponseResult(200, "更新成功");
        }
        return new ResponseResult(400, "更新失败，用户不存在");
    }


    @Override
    public ResponseResult registerPersonal(User user) {
        if (user.getUsername() != null){
            if (judgeRepeatPhone(user.getUsername(), -1L)){
                return new ResponseResult(400, "注册失败，手机号已注册");
            }
        }
        if(user.getEmail() != null){
            if (judgeRepeatEmail(user.getEmail(), -1L)){
                return new ResponseResult(400, "注册失败，邮箱已注册");
            }
        }
        long roleId;
        if(user.getRoleName() != null && !user.getRoleName().isEmpty() && user.getRoleName().equals("admin")){
            roleId = 1L;
        }
        else {
            roleId = 7L;
        }
        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);
        int x = userMapper.insert(user);

        UserRole userRole = new UserRole();
        userRole.setRoleId(roleId);
        userRole.setUserId(user.getId());
        userRoleMapper.insert(userRole);

        return new ResponseResult(200, "注册成功");
    }

    @Override
    public ResponseResult<User> selectPersonalUser() {
        Long nowUserId = SystemCommonUtil.getNowUserId();
        User user = userMapper.selectPersonalUser(nowUserId);
        if (user == null) {
            return new ResponseResult<>(404, "用户不存在");
        }
        user.setPassword(null);
        user.setStatus(user.getIsDelete() != null && user.getIsDelete().equals(0) ? 1 : 0);
        return new ResponseResult<>(200, user);
    }

    @Override
    public ResponseResult<TableData<User>> selectSystemUser(UserSelectDTO dto) {
        int pageNum = dto == null || dto.getPageNum() == null ? 1 : dto.getPageNum();
        int pageSize = dto == null || dto.getPageSize() == null ? 10 : dto.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userMapper.selectSystemUser(dto);
        if (list != null) {
            for (User u : list) {
                if (u == null) {
                    continue;
                }
                u.setPassword(null);
                u.setStatus(u.getIsDelete() != null && u.getIsDelete().equals(0) ? 1 : 0);
            }
        }
        TableData<User> tableData = new TableData<>(list);
        return new ResponseResult<>(200, "查询成功", tableData);
    }

    @Override
    public ResponseResult<Void> updateUser(User user) {
        if (user == null || user.getId() == null) {
            return new ResponseResult<>(400, "更新失败，缺少用户id");
        }

        Long nowUserId = SystemCommonUtil.getNowUserId();
        boolean isAdmin = SystemCommonUtil.hasPerm("all");
        if (!isAdmin && !user.getId().equals(nowUserId)) {
            return new ResponseResult<>(403, "无权限");
        }

        if (user.getUsername() != null && judgeRepeatPhone(user.getUsername(), user.getId())) {
            return new ResponseResult<>(400, "更新失败，手机号已注册");
        }
        if (user.getEmail() != null && judgeRepeatEmail(user.getEmail(), user.getId())) {
            return new ResponseResult<>(400, "更新失败，邮箱已注册");
        }

        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, user.getId());
        if (user.getName() != null) {
            wrapper.set(User::getName, user.getName());
        }
        if (user.getUsername() != null) {
            wrapper.set(User::getUsername, user.getUsername());
        }
        if (user.getEmail() != null) {
            wrapper.set(User::getEmail, user.getEmail());
        }
        if (user.getStatus() != null) {
            if (!isAdmin) {
                return new ResponseResult<>(403, "无权限修改状态");
            }
            wrapper.set(User::getIsDelete, user.getStatus().equals(1) ? 0 : 1);
        }
        wrapper.set(User::getUpdateBy, nowUserId);

        int x = userMapper.update(null, wrapper);
        if (x > 0 && user.getRoleId() != null) {
            if (!isAdmin) {
                return new ResponseResult<>(403, "无权限修改角色");
            }
            LambdaQueryWrapper<UserRole> urQuery = new LambdaQueryWrapper<>();
            urQuery.eq(UserRole::getUserId, user.getId());
            urQuery.eq(UserRole::getIsDelete, 0);
            UserRole ur = userRoleMapper.selectOne(urQuery);
            if (ur == null) {
                ur = new UserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(user.getRoleId());
                ur.setIsDelete(0);
                ur.setUpdateBy(nowUserId);
                userRoleMapper.insert(ur);
            } else {
                LambdaUpdateWrapper<UserRole> urUpdate = new LambdaUpdateWrapper<>();
                urUpdate.eq(UserRole::getId, ur.getId());
                urUpdate.set(UserRole::getRoleId, user.getRoleId());
                urUpdate.set(UserRole::getUpdateBy, nowUserId);
                userRoleMapper.update(null, urUpdate);
            }
        }
        if (x > 0) {
            return new ResponseResult<>(200, "更新成功");
        }
        return new ResponseResult<>(400, "更新失败，用户不存在");
    }

    @Override
    public ResponseResult<Void> deleteUser(Long id) {
        if (id == null) {
            return new ResponseResult<>(400, "删除失败，缺少用户id");
        }
        if (!SystemCommonUtil.hasPerm("all")) {
            return new ResponseResult<>(403, "无权限");
        }
        Long nowUserId = SystemCommonUtil.getNowUserId();

        LambdaUpdateWrapper<User> userWrapper = new LambdaUpdateWrapper<>();
        userWrapper.eq(User::getId, id);
        userWrapper.set(User::getIsDelete, 1);
        userWrapper.set(User::getUpdateBy, nowUserId);
        userMapper.update(null, userWrapper);

        LambdaUpdateWrapper<UserRole> urWrapper = new LambdaUpdateWrapper<>();
        urWrapper.eq(UserRole::getUserId, id);
        urWrapper.set(UserRole::getIsDelete, 1);
        urWrapper.set(UserRole::getUpdateBy, nowUserId);
        userRoleMapper.update(null, urWrapper);

        return new ResponseResult<>(200, "删除成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<Void> insertUser(User user) {
        if (!SystemCommonUtil.hasPerm("all")) {
            return new ResponseResult<>(403, "无权限");
        }
        if (user == null) {
            return new ResponseResult<>(400, "添加失败");
        }
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            return new ResponseResult<>(400, "添加失败，请填写手机号");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            return new ResponseResult<>(400, "添加失败，请填写用户名称");
        }
        if (user.getRoleId() == null) {
            return new ResponseResult<>(400, "添加失败，请选择用户角色");
        }

        if (judgeRepeatPhone(user.getUsername(), -1L)) {
            return new ResponseResult<>(400, "添加失败，手机号已注册");
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            if (judgeRepeatEmail(user.getEmail(), -1L)) {
                return new ResponseResult<>(400, "添加失败，邮箱已注册");
            }
        }

        String rawPassword = user.getPassword();
        if (rawPassword == null || rawPassword.isBlank()) {
            rawPassword = DEFAULT_PASSWORD;
        }
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setIsDelete(0);
        user.setUpdateBy(SystemCommonUtil.getNowUserId());
        userMapper.insert(user);

        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(user.getRoleId());
        userRole.setIsDelete(0);
        userRole.setUpdateBy(SystemCommonUtil.getNowUserId());
        userRoleMapper.insert(userRole);

        return new ResponseResult<>(200, "添加成功");
    }

    @Override
    public ResponseResult<User> selectUserById(Long id) {
        if (id == null) {
            return new ResponseResult<>(400, "参数错误");
        }
        if (!SystemCommonUtil.hasPerm("all")) {
            return new ResponseResult<>(403, "无权限");
        }
        User user = userMapper.selectUserById(id);
        if (user == null) {
            return new ResponseResult<>(404, "用户不存在");
        }
        user.setPassword(null);
        user.setStatus(user.getIsDelete() != null && user.getIsDelete().equals(0) ? 1 : 0);
        return new ResponseResult<>(200, user);
    }

    private boolean judgeRepeatPhone(String phone, Long id) {
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getUsername, phone);
        userWrapper.eq(User::getIsDelete, 0);
        userWrapper.ne(User::getId, id);
        User user = userMapper.selectOne(userWrapper);
        return user != null;
    }

    private boolean judgeRepeatEmail(String email, Long id){
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getEmail, email);
        userWrapper.eq(User::getIsDelete, 0);
        userWrapper.ne(User::getId, id);
        User user = userMapper.selectOne(userWrapper);
        return user != null;
    }

}
