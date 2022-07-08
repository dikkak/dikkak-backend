package com.dikkak.dto.auth;

import com.dikkak.entity.ProviderTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogoutRes {

    private ProviderTypeEnum provider;
}
