package com.dikkak.controller.coworking;

import com.dikkak.config.UserPrincipal;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.user.UserTypeEnum;
import com.dikkak.service.coworking.CoworkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoworkingSupport {
    private final CoworkingService coworkingService;

    // 작업실 접근권한이 있는 회원인지 검사 후 Coworking 반환
    @Nullable
    public Coworking checkUserAndGetCoworking(UserPrincipal principal, Long coworkingId) {
        UserTypeEnum type = principal.getType();
        if (type.equals(UserTypeEnum.UNDEFINED)) return null;

        Coworking coworking = coworkingService.getCoworking(coworkingId);

        if (type.equals(UserTypeEnum.ADMIN)) return coworking;
        if (type.equals(UserTypeEnum.CLIENT)) {
            if (!principal.getUserId().equals(coworking.getProposal().getClient().getId())) {
                return null;
            }
        }
        if (type.equals(UserTypeEnum.DESIGNER)) {
            if (!principal.getUserId().equals(coworking.getDesigner().getId())) {
                return null;
            }
        }
        return coworking;
    }
}
