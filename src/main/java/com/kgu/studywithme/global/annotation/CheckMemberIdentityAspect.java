package com.kgu.studywithme.global.annotation;

import com.kgu.studywithme.auth.exception.AuthErrorCode;
import com.kgu.studywithme.global.exception.StudyWithMeException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CheckMemberIdentityAspect {
    @Before("@annotation(com.kgu.studywithme.global.annotation.CheckMemberIdentity) && args(memberId, payloadId, ..)")
    public void checkPayloadId(Long memberId, Long payloadId) {
        if (isAnonymousMember(memberId, payloadId)) {
            throw StudyWithMeException.type(AuthErrorCode.INVALID_PERMISSION);
        }
    }

    private boolean isAnonymousMember(Long memberId, Long payloadId) {
        return !memberId.equals(payloadId);
    }
}
