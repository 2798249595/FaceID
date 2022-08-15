package com.wllt.faceid.core.db.domain;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName standby
 */
@Data
public class Standby implements Serializable {
    /**
     *
     */
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 1图片2视频
     */
    private Integer type;

    /**
     * 图片文件名
     */
    private String image;

    /**
     * 视频名
     */
    private String video;

    /**
     * 图片宽
     */
    private String imagewide;

    /**
     * 图片高
     */
    private String imagehign;

    private static final long serialVersionUID = 1L;

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
        Standby other = (Standby) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getImage() == null ? other.getImage() == null : this.getImage().equals(other.getImage()))
            && (this.getVideo() == null ? other.getVideo() == null : this.getVideo().equals(other.getVideo()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getImage() == null) ? 0 : getImage().hashCode());
        result = prime * result + ((getVideo() == null) ? 0 : getVideo().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", title=").append(title);
        sb.append(", type=").append(type);
        sb.append(", image=").append(image);
        sb.append(", video=").append(video);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
