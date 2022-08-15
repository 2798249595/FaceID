package com.wllt.faceid.core.db.domain;

import java.io.Serializable;

/**
 * 
 * @TableName equipment
 */
public class Equipment implements Serializable {
    /**
     * 设备状态
     */
    private Integer id;

    /**
     * 是否激活
     */
    private String isActive;

    /**
     * 1在线,2离线,3禁用
     */
    private Integer status;

    private static final long serialVersionUID = 1L;

    /**
     * 设备状态
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设备状态
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 是否激活
     */
    public String getIsActive() {
        return isActive;
    }

    /**
     * 是否激活
     */
    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    /**
     * 1在线,2离线,3禁用
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 1在线,2离线,3禁用
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Equipment other = (Equipment) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getIsActive() == null ? other.getIsActive() == null : this.getIsActive().equals(other.getIsActive()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getIsActive() == null) ? 0 : getIsActive().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", isActive=").append(isActive);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}