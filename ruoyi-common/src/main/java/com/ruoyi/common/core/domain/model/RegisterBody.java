package com.ruoyi.common.core.domain.model;

/**
 * 用户注册对象
 * 
 * @author ruoyi
 */
public class RegisterBody extends LoginBody
{
    /** 注册身份（user/doctor） */
    private String registerRole;

    /** 申请科室（医生注册使用） */
    private Long deptId;

    public String getRegisterRole()
    {
        return registerRole;
    }

    public void setRegisterRole(String registerRole)
    {
        this.registerRole = registerRole;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

}
