package com.dikkak.entity.proposal;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Keyword {

    @Id @GeneratedValue
    @Column(name = "keyword_id")
    private Long id;

    @Column(unique = true)
    private String name;

    public Keyword(String name) {
        this.name = name;
    }
}
