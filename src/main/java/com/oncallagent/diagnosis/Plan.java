package com.oncallagent.diagnosis;

import java.util.List;

/** 诊断计划:有序的步骤列表。 */
public record Plan(List<String> steps) {}
