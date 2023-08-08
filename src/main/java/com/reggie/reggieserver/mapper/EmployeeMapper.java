package com.reggie.reggieserver.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.reggieserver.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
