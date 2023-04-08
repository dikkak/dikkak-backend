package com.dikkak.dto.mail;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class MailDto {
    private List<String> emailList;
    private String title;
    private String content;

    @Builder
    public MailDto(List<String> emailList, String title, String content) {
        this.emailList = emailList;
        this.title = title;
        this.content = content;
    }
}
