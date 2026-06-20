package com.oncallagent.diagnosis;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/** 诊断编排:纯 Java 手搭 plan-execute-replan。 */
@Service
public class DiagnosisService {

    private static final int MAX_STEPS = 6;

    private final Planner planner;
    private final Executor executor;
    private final Replanner replanner;
    private final Reporter reporter;

    public DiagnosisService(
            Planner planner, Executor executor, Replanner replanner, Reporter reporter) {
        this.planner = planner;
        this.executor = executor;
        this.replanner = replanner;
        this.reporter = reporter;
    }

    public DiagnosisReport run(String goal) {
        Plan plan = planner.plan(goal);
        List<PastStep> pastSteps = new ArrayList<>();
        for (int i = 0; i < MAX_STEPS; i++) {
            if (plan.steps().isEmpty()) {
                break;
            }
            String step = plan.steps().get(0);
            pastSteps.add(new PastStep(step, executor.execute(goal, step, pastSteps)));
            ReplanDecision decision = replanner.replan(goal, pastSteps);
            if (decision.finished()) {
                return new DiagnosisReport(decision.report(), pastSteps);
            }
            plan =
                    new Plan(
                            decision.remainingSteps() == null
                                    ? List.of()
                                    : decision.remainingSteps());
        }
        return new DiagnosisReport(reporter.report(goal, pastSteps), pastSteps); // MAX_STEPS 护栏
    }
}
