package com.tranyu.ltc.inner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LtcInnerUser {
    private Long targetId;
    private String userName;
    private String mobile;
    private Long deptId;
    private String unionId;
    private String fsMobile;

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getUnionId() { return unionId; }
    public void setUnionId(String unionId) { this.unionId = unionId; }
    public String getFsMobile() { return fsMobile; }
    public void setFsMobile(String fsMobile) { this.fsMobile = fsMobile; }
}
