package com.kgu.studywithme.member.infra.query;

public interface MemberSimpleQueryRepository {
    boolean isReportReceived(Long reporteeId, Long reporterId);
}
