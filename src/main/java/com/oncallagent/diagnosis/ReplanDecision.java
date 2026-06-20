package com.oncallagent.diagnosis;

import java.util.List;

/** 重规划决策:finished=true 给报告;否则给剩余步骤。 */
public record ReplanDecision(boolean finished, String report, List<String> remainingSteps) {}
