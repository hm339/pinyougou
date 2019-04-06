package cn.itcast.core.pojo.entity;

import java.io.Serializable;

/**
 * 定义实体类, 返回操作是否成功信息
 */
public class Result implements Serializable {
    //返回是否成功状态, true成功, FALSE操作不成功
    private boolean success;
    //保存成功信息或者不成功信息
    private String message;

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
