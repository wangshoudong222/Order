package com.julihe.order.model.result;

public class Config {
    /**
     * 设备id
     */
    private String deviceId;
    /**
     * 食堂id
     */
    private String kitchenId;
    /**
     * 食堂名称
     */
    private String kitchenName;
    /**
     * 点餐模式
     */
    private String orderMode;
    /**
     * 点餐模式名称
     */
    private String orderModeName;
    /**
     * 学校id
     */
    private String schoolId;
    /**
     * 学校名称
     */
    private String schoolName;
    /**
     * 窗口id
     */
    private String windowId;
    /**
     * 窗口名称
     */
    private String windowName;
    /**
     * 设备配置id
     */
    private String deviceConfigId;
    /**
     * 学校人脸库id
     */
    private String schoolFaceId;
    /**
     * 学校内标
     */
    private String instId;
    /**
     * 学校支付宝用户id
     */
    private String schoolPaymentUserId;
    
    public String getDeviceConfigId() { return deviceConfigId; }
    public void setDeviceConfigId(String value) { this.deviceConfigId = value; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String value) { this.deviceId = value; }

    public String getInstId() { return instId; }
    public void setInstId(String value) { this.instId = value; }

    public String getKitchenId() { return kitchenId; }
    public void setKitchenId(String value) { this.kitchenId = value; }

    public String getKitchenName() { return kitchenName; }
    public void setKitchenName(String value) { this.kitchenName = value; }

    public String getOrderMode() { return orderMode; }
    public void setOrderMode(String value) { this.orderMode = value; }

    public String getOrderModeName() { return orderModeName; }
    public void setOrderModeName(String value) { this.orderModeName = value; }

    public String getSchoolFaceId() { return schoolFaceId; }
    public void setSchoolFaceId(String value) { this.schoolFaceId = value; }

    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String value) { this.schoolId = value; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String value) { this.schoolName = value; }

    public String getSchoolPaymentUserId() { return schoolPaymentUserId; }
    public void setSchoolPaymentUserId(String value) { this.schoolPaymentUserId = value; }

    public String getWindowId() { return windowId; }
    public void setWindowId(String value) { this.windowId = value; }

    public String getWindowName() { return windowName; }
    public void setWindowName(String value) { this.windowName = value; }

}
