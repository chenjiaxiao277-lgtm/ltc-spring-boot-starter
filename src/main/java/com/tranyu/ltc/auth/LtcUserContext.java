package com.tranyu.ltc.auth;

public class LtcUserContext {
    private Long targetId;
    private Long deptId;
    private String displayName;

    public LtcUserContext() {}

    public LtcUserContext(Long targetId, Long deptId, String displayName) {
        this.targetId = targetId;
        this.deptId = deptId;
        this.displayName = displayName;
    }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}
