package com.analytics.sdk.service.report;

import com.analytics.sdk.common.helper.Listener;
import com.analytics.sdk.service.ServiceManager;
import com.analytics.sdk.service.report.entity.ReportData;

public final class IReportServiceHelper {

    public static void report(ReportData reportData){
        final IReportService reportService = ServiceManager.getService(IReportService.class);
        reportService.report(reportData,Listener.ONLY_LOG);
    }

    public static void report(ReportData reportData, Listener listener){
        final IReportService reportService = ServiceManager.getService(IReportService.class);
        reportService.report(reportData,listener);
    }



}
