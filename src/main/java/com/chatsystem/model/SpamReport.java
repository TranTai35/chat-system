package com.chatsystem.model;

import java.time.LocalDateTime;

/** Một báo cáo spam kèm thông tin người bị báo cáo và người báo cáo, dùng cho màn A4. */
public class SpamReport {

    public enum Status { PENDING, RESOLVED }

    private int id;
    private LocalDateTime createdAt;
    private int reportedId;
    private String reportedUsername;
    private String reportedFullName;
    private boolean reportedLocked;
    private int reporterId;
    private String reporterUsername;
    private String reason;
    private Status status;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getReportedId() { return reportedId; }
    public void setReportedId(int reportedId) { this.reportedId = reportedId; }

    public String getReportedUsername() { return reportedUsername; }
    public void setReportedUsername(String reportedUsername) { this.reportedUsername = reportedUsername; }

    public String getReportedFullName() { return reportedFullName; }
    public void setReportedFullName(String reportedFullName) { this.reportedFullName = reportedFullName; }

    /** Tài khoản bị báo cáo hiện có đang bị khoá không (cột "Trạng thái tài khoản"). */
    public boolean isReportedLocked() { return reportedLocked; }
    public void setReportedLocked(boolean reportedLocked) { this.reportedLocked = reportedLocked; }

    public int getReporterId() { return reporterId; }
    public void setReporterId(int reporterId) { this.reporterId = reporterId; }

    public String getReporterUsername() { return reporterUsername; }
    public void setReporterUsername(String reporterUsername) { this.reporterUsername = reporterUsername; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
