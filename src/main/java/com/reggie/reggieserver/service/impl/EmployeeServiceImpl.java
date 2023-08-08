package com.reggie.reggieserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.reggieserver.entity.Employee;
import com.reggie.reggieserver.mapper.EmployeeMapper;
import com.reggie.reggieserver.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
