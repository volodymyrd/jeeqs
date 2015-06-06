package com.gmail.volodymyrdotsenko.phonebilling.tools;

import java.util.HashMap;
import java.util.Map;

import javax.batch.runtime.Metric;

public class BatchTestHelper {
	public static Map<Metric.MetricType, Long> getMetricsMap(Metric[] metrics) {
		Map<Metric.MetricType, Long> metricsMap = new HashMap<>();
		for (Metric metric : metrics) {
			metricsMap.put(metric.getType(), metric.getValue());
		}
		return metricsMap;
	}
}