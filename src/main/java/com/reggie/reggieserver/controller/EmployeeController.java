package com.reggie.reggieserver.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.reggieserver.common.Result;
import com.reggie.reggieserver.entity.Employee;
import com.reggie.reggieserver.service.EmployeeService;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name) {
        log.info("page={},pageSize={},name={}", page, pageSize, name);
        //构造分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        //添加过滤条件（当我们没有输入name时，就相当于查询所有了）
        wrapper.like(!(name == null || "".equals(name)), Employee::getName, name);
        //并对查询的结果进行降序排序，根据更新时间
        wrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo, wrapper);
        return Result.success(pageInfo);
    }


    @PostMapping("/save")
    public Result<Employee> save(HttpServletRequest request,@RequestBody Employee employee){
        System.out.println("employee = " + employee);
        String originPwd = employee.getPassword();
        employee.setPassword(DigestUtils.md5DigestAsHex(originPwd.getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        Long curUserId =(Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(curUserId);
        employee.setUpdateUser(curUserId);
        employeeService.save(employee);
        return  Result.success(employee);
    }

    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        System.out.println("request = " + request + ", employee = " + employee);
        String password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());
        //这部分就是MP
        LambdaQueryWrapper<Employee> sqlQuery = new LambdaQueryWrapper<>();
        sqlQuery.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(sqlQuery);
        if (emp == null) {
            return Result.error("登录失败");
        }
        if (!emp.getPassword().equals(password)) {
            return Result.error("密码错误");
        }
        if (emp.getStatus() == 0) {
            return Result.error("该用户已被禁用");
        }

        request.getSession().setAttribute("employee", emp.getId());
        return Result.success(emp);
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return Result.success("退出登录成功");
    }


}
