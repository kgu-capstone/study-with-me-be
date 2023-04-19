package com.kgu.studywithme.global.annotation;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CheckMemberIdentityAspect {
    @Before("@annotation(CheckMemberIdentity) && args(payloadId, memberId, ..)")
    public void checkPayloadId(Long payloadId, Long memberId) {
        if (isAnonymousMember(payloadId, memberId)) {
            throw StudyWithMeException.type(AuthErrorCode.INVALID_PERMISSION);
        }
    }

    private boolean isAnonymousMember(Long payloadId, Long memberId) {
        return !payloadId.equals(memberId);
    }
}
