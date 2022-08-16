package com.dikkak.repository.proposal;

import com.dikkak.dto.workplace.ClientWorkplaceRes;

import java.util.List;

public interface ProposalRepositoryCustom {
    List<ClientWorkplaceRes> getClientWorkplace(Long clientId);

}
