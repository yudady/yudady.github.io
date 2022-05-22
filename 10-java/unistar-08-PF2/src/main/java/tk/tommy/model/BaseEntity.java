package tk.tommy.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

@MappedSuperclass
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = -7795131620141811534L;
    @Version
    @Column(name = "VERSION", columnDefinition = "Number(12,0)")
    private Long version;
    /**
     * 新增時間
     */
    @Column(name = "CREATE_TIME", updatable = false)
    private Date createTime;
    /**
     * 更新時間
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    public Long getVersion() {
        return this.version;
    }

    public void setVersion(final Long version) {
        this.version = version;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(final Date updateTime) {
        this.updateTime = updateTime;
    }

    @PrePersist
    public void onCreate() {
        final Date now = new Date();
        this.createTime = this.createTime == null ? now : this.createTime;
        this.updateTime = this.updateTime == null ? now : this.updateTime;
    }

    @PreUpdate
    public void onUpdate() {
        this.updateTime = new Date();
    }
}