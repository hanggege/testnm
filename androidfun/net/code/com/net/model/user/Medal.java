package com.net.model.user;

/**
 * @ProjectName: dove
 * @Package: com.net.model.user
 * @ClassName: Medal
 * @Description:
 * @Author: zxj
 * @CreateDate: 2020/6/1 15:25
 * @UpdateUser:
 * @UpdateDate: 2020/6/1 15:25
 * @UpdateRemark:
 * @Version:
 */
public class Medal {

    private String medal;
    private int priority;
    private int publisherId;

    public String getMedal() {
        return medal;
    }

    public void setMedal(String medal) {
        this.medal = medal;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }
}
