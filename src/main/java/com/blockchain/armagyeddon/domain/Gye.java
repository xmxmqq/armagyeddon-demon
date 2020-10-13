package com.blockchain.armagyeddon.domain;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Gye {

    Long id;

    String type;

    String interest;

    String title;

    int targetMoney;

    int period;

    LocalDateTime payDay;

    int totalMember;

    String state;

    String master;


    List<Member> members = new ArrayList<Member>();
}
