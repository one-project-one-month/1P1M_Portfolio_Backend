package com._p1m.portfolio.model;

import com._p1m.portfolio.common.entity.MasterData;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OpomRegister extends MasterData {

    private String name;
    private String email;
    private String phone;
    private String github_url;
    private String telegram_username;
    private String role;
}
