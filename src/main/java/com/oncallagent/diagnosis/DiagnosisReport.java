package com.oncallagent.diagnosis;

import java.util.List;

/** 诊断结果:Markdown 报告 + 完整执行轨迹。 */
public record DiagnosisReport(String report, List<PastStep> trace) {}
