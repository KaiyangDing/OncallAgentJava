package com.oncallagent.api;

/**
 * 统一 API 响应信封。
 *
 * <p>所有 HTTP 接口都返回此结构,调用方根据 {@code success} 区分成败: 成功取 {@code data}、失败取 {@code error},契约稳定。
 *
 * @param success 是否成功
 * @param data 成功时的业务数据;失败时为 null
 * @param error 失败时的错误信息;成功时为 null
 * @param <T> 业务数据的类型
 */
public record ApiResponse<T>(boolean success, T data, ErrorInfo error) {

    /**
     * 结构化错误信息。
     *
     * @param code 机器可判断的错误码(如 "NOT_FOUND")
     * @param message 给人看的错误描述
     */
    public record ErrorInfo(String code, String message) {}

    /** 构造成功响应。 */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    /** 构造失败响应。 */
    public static <T> ApiResponse<T> fail(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorInfo(code, message));
    }
}
