package com.dikkak.controller.coworking;

import com.dikkak.common.BaseException;
import com.dikkak.config.UserPrincipal;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.user.UserTypeEnum;
import com.dikkak.service.coworking.CoworkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoworkingSupport {
    private final CoworkingService coworkingService;

    // 작업실 접근권한이 있는 회원인지 검사
    public boolean checkUser(UserPrincipal principal, Long coworkingId) throws BaseException {
        UserTypeEnum type = principal.getType();
        if (type.equals(UserTypeEnum.ADMIN)) return true;
        if (type.equals(UserTypeEnum.UNDEFINED)) return false;
        else {
            Coworking coworking = coworkingService.getCoworking(coworkingId);
            if (type.equals(UserTypeEnum.CLIENT))
                return principal.getUserId().equals(coworking.getProposal().getClient().getId());
            else if (type.equals(UserTypeEnum.DESIGNER))
                return principal.getUserId().equals(coworking.getDesigner().getId());
        }
        return false;
    }
}
