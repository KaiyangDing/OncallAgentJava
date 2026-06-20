package com.oncallagent.diagnosis;

import com.oncallagent.common.ApiResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** AIOps 诊断接口。 */
@RestController
public class DiagnosisController {

    private static final String DEFAULT_GOAL = "诊断当前活跃的告警,定位根因并给出处置建议";

    private final DiagnosisService diagnosisService;

    public DiagnosisController(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    /** 触发一次完整诊断(plan-execute-replan)。空 body 则自动巡检当前告警。 */
    @PostMapping("/api/diagnosis")
    public ApiResponse<DiagnosisReport> diagnose(
            @RequestBody(required = false) DiagnosisRequest request) {
        String goal =
                (request != null && StringUtils.hasText(request.goal()))
                        ? request.goal()
                        : DEFAULT_GOAL;
        return ApiResponse.ok(diagnosisService.run(goal));
    }

    public record DiagnosisRequest(String goal) {}
}
