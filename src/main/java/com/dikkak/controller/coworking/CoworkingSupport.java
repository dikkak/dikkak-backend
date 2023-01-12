package com.dikkak.controller.coworking;

import com.dikkak.common.BaseException;
import com.dikkak.config.UserPrincipal;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.user.UserTypeEnum;
import org.springframework.stereotype.Component;

import static com.dikkak.common.ResponseMessage.UNAUTHORIZED_REQUEST;

@Component
public class CoworkingSupport {
    // 작업실 접근권한이 있는 회원인지 검사
    public void checkCoworkingUser(UserPrincipal principal, Coworking coworking) {
        UserTypeEnum type = principal.getType();
        Long userId = principal.getUserId();

        if (type == UserTypeEnum.UNDEFINED) {
            throw new BaseException(UNAUTHORIZED_REQUEST);
        }
        if (type == UserTypeEnum.CLIENT && !isEqualsClient(userId, coworking)) {
            throw new BaseException(UNAUTHORIZED_REQUEST);
        }
        if (type == UserTypeEnum.DESIGNER && !isEqualsDesigner(userId, coworking)) {
            throw new BaseException(UNAUTHORIZED_REQUEST);
        }
    }

    private static boolean isEqualsClient(Long clientId, Coworking coworking) {
        return clientId.equals(coworking.getProposal().getClient().getId());
    }

    private static boolean isEqualsDesigner(Long designerId, Coworking coworking) {
        return designerId.equals(coworking.getDesigner().getId());
    }
}
