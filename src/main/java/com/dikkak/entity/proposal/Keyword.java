package com.dikkak.entity.proposal;

import javax.persistence.*;

@Entity
public class Keyword {

    @Id @GeneratedValue
    @Column(name = "keyword_id")
    private Long id;

    @Column(unique = true)
    private String name;
}
