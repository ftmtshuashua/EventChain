package com.acap.demo.mode;

/**
 * <pre>
 * Tip:
 *      用户信息
 *
 * Created by ACap on 2021/7/3 16:50
 * </pre>
 */
public class ModelUserDetail {
    public String detail;

    public ModelUserDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "ModelUserDetail{" +
                "detail='" + detail + '\'' +
                '}';
    }
}
