package com.dikkak.repository.coworking.message;

import com.dikkak.dto.coworking.GetChattingRes;
import com.dikkak.entity.coworking.Coworking;

import java.util.List;

public interface CoworkingMessageRepositoryCustom {

    List<GetChattingRes> getCoworkingMessage(Coworking coworking);
}
