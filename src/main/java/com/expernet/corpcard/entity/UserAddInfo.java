package com.expernet.corpcard.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "USER_ADD_INFO")
@NoArgsConstructor
public class UserAddInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private long seq;

    @JsonBackReference
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name ="USER_ID", referencedColumnName = "USER_ID")
    private User user;

    @Column(name = "USER_PASSWD")
    private String userPasswd;

    @Column(name = "ADMIN_YN", columnDefinition = "char")
    private String adminYn;

    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @Builder
    public UserAddInfo(long seq, String userPasswd, String adminYn, User user) {
        this.seq = seq;
        this.userPasswd = userPasswd;
        this.adminYn = adminYn;
        this.user = user;
    }
}
