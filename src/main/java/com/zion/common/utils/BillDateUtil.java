package com.zion.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class BillDateUtil {
    public static LocalDateTime parseStartDate(String dateStr) {
        if (dateStr == null) {
            return LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        }

        try {
            // 尝试 ISO 标准格式（yyyy-MM-dd）
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        } catch (Exception e) {
            // 尝试其他格式（如 yyyy/MM/dd）
            DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            return LocalDate.parse(dateStr, customFormatter).atStartOfDay();
        }
    }

    public static LocalDateTime parseEndDate(String endDay) {
        if (endDay == null) {
            return LocalDateTime.now().with(LocalTime.MAX);
        }

        try {
            // 尝试 ISO 标准格式（yyyy-MM-dd）
            return LocalDate.parse(endDay, DateTimeFormatter.ISO_LOCAL_DATE).atTime(LocalTime.MAX);
        } catch (Exception e) {
            // 尝试其他格式（如 yyyy/MM/dd）
            DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            return LocalDate.parse(endDay, customFormatter).atTime(LocalTime.MAX);
        }
    }

}
