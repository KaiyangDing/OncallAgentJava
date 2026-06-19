package com.oncallagent.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 健康检查接口。 */
@RestController
public class HealthController {

    /** 存活探针:返回服务状态,用来验证整条 Web 链路通畅。 */
    @GetMapping("/api/health")
    public ApiResponse<HealthStatus> health() {
        return ApiResponse.ok(new HealthStatus("UP"));
    }

    /** 健康状态载荷。 */
    public record HealthStatus(String status) {}
}
