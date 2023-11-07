package com.example.registeruser.verification;

import com.example.registeruser.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String verificationToken;
    private Date expirationTime;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public VerificationToken(String verificationToken, User user) {
        this.verificationToken = verificationToken;
        this.user = user;
        this.expirationTime = ExpirationTimeToken.getExpirationTime();
    }
}
