package com.dikkak.repository.proposal;

import com.dikkak.dto.workplace.WorkplaceRes;

public interface ProposalRepositoryCustom {

    WorkplaceRes getByUserId(Long userId);
}
