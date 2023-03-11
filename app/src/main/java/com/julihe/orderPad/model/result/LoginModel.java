package com.julihe.orderPad.model.result;

public class LoginModel {
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 登录后端返回token
     */
    private String token;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户账号
     */
    private String userName;

    public String getRealName() { return realName; }
    public void setRealName(String value) { this.realName = value; }

    public String getToken() { return token; }
    public void setToken(String value) { this.token = value; }

    public String getUserId() { return userId; }
    public void setUserId(String value) { this.userId = value; }

    public String getUserName() { return userName; }
    public void setUserName(String value) { this.userName = value; }
}


