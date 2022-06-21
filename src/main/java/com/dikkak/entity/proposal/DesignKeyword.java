package com.dikkak.entity.proposal;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class DesignKeyword {

    @Id @GeneratedValue
    @Column(name = "keyword_id")
    private Long id;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "keyword")
    List<Proposal_DesignKeyword> proposals = new ArrayList<>();

}
