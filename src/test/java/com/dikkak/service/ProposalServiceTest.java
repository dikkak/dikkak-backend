package com.dikkak.service;

import com.dikkak.dto.admin.GetUserProposalsRes;
import com.dikkak.dto.proposal.PostProposalReq;
import com.dikkak.entity.proposal.CategoryEnum;
import com.dikkak.entity.user.ProviderTypeEnum;
import com.dikkak.entity.user.User;
import com.dikkak.entity.user.UserTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProposalServiceTest {

    @Autowired
    ProposalService proposalService;
    @Autowired
    UserService userService;

    @Test
    void getProposalList() {
        try {

            User saveUser = userService.create(User.builder().email("test@gmail.com").providerType(ProviderTypeEnum.GOOGLE).build());
            System.out.println("saveUser = " + saveUser.getId());

            saveUser.setUserType(UserTypeEnum.ADMIN);


            for (int i = 1; i < 4; i++) {
                PostProposalReq req = new PostProposalReq();
                req.setCategory(CategoryEnum.LOGO_OR_CARD);
                req.setDeadline("2022-08-01");
                req.setTitle("제목" + i);
                req.setMainColor("#ffffff");
                req.setPurpose("목적" + i);
                proposalService.create(saveUser, req);
            }


            for (GetUserProposalsRes proposalsRes : proposalService.getUserProposalList(saveUser.getId())) {
                System.out.println("proposalsRes = " + proposalsRes);
            }
        } catch (Exception e) {
            System.out.println("e.getMessage() = " + e.getMessage());
        }

    }
}