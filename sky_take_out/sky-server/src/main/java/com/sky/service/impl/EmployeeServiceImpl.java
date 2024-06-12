package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import com.sky.util.MailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private MailService mailService;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 使用Spring中工具类对密码进行加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public String save(EmployeeDTO dto) {
        Employee employee = new Employee();
        String pass = "";

        //对象属性拷贝
        BeanUtils.copyProperties(dto, employee);
        //设置账号的状态，默认正常状态 1表示正常 0表示锁定
        employee.setStatus(StatusConstant.ENABLE);
        //设置密码，默认密码123456
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        pass = PasswordConstant.DEFAULT_PASSWORD;
        //设置当前记录的创建时间和修改时间
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //获取拦截器中设置当前记录创建人id和修改人id
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());
        
//        try {
            employeeMapper.insert(employee);
//        } catch (Exception e) {
//            mailService.sendTextMailMessage(dto.getEmail(), "用户管理平台", "您刚才注册的"+dto.getUsername()+"账号注册失败，请切换用户名后重新注册");
//            return "注册失败";
//        }
        // 没有填写邮箱，返回默认密码给前端，否则发送邮件
//        if (dto.getEmail() != null) {
//            try {
//                mailService.sendTextMailMessage(dto.getEmail(), "用户管理平台", "您刚注册的账号密码为：4525");
//                pass = "4525";
//            } catch (Exception ex) {
//                System.out.println(ex);
//            }
//        }
        return pass;
    }

    /**
     * startPage 与 pageQuery如何实现关联
     *
     * @param dto
     * @return {@link PageResult}
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO dto) {
        // 基于Mybatis拦截器动态分页
        PageHelper.startPage(dto.getPage(), dto.getPageSize());

        Page<Employee> pageInfo = employeeMapper.pageQuery(dto);

        return new PageResult(pageInfo.getTotal(), pageInfo.getResult());
    }
}
