package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/save")
    public Result addUser(@RequestBody EmployeeDTO dto) {
        log.info("新增员工：{}", dto);
        employeeService.save(dto);
        return Result.success();
    }

    @GetMapping("pageList")
    public Result<PageResult> pageList(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("员工分页查询，参数为：{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工，id为：{}", id);
        return Result.success(employeeService.getById(id));
    }

    @PutMapping
    public Result update(@RequestBody EmployeeDTO dto){
        employeeService.update(dto);
        return Result.success();
    }


    /**
     * 启用禁用
     * http://localhost:8080/admin/employee/enable/0?id=10
     *
     * @param status
     * @param id
     * @return {@link Result}
     */
    @PostMapping("/enable/{status}")
    @ApiOperation("启用禁用员工账号")
    public Result enable(@PathVariable("status") Integer status, Long id) {
        employeeService.enable(status, id);
        return Result.success();

    }

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);


        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

}
