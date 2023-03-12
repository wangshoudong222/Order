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

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String value) { this.deviceId = value; }

    public String getKitchenId() { return kitchenId; }
    public void setKitchenId(String value) { this.kitchenId = value; }

    public String getKitchenName() { return kitchenName; }
    public void setKitchenName(String value) { this.kitchenName = value; }

    public String getOrderMode() { return orderMode; }
    public void setOrderMode(String value) { this.orderMode = value; }

    public String getOrderModeName() { return orderModeName; }
    public void setOrderModeName(String value) { this.orderModeName = value; }

    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String value) { this.schoolId = value; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String value) { this.schoolName = value; }

    public String getWindowId() { return windowId; }
    public void setWindowId(String value) { this.windowId = value; }

    public String getWindowName() { return windowName; }
    public void setWindowName(String value) { this.windowName = value;
    }
}
