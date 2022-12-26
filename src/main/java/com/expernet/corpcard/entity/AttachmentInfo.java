package com.expernet.corpcard.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Entity
@Table(name = "ATTACHMENT_INFO")
@NoArgsConstructor
public class AttachmentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private long seq;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SUBMIT_SEQ")
    private UsehistSubmitInfo usehistSubmitInfo;

    @Column(name = "FILE_NM")
    private String fileNm;

    @Column(name = "FILE_EXT_NM")
    private String fileExtNm;

    @Column(name = "FILE_PATH")
    private String filePath;

    @Column(name = "UPLOAD_FN")
    private long uploadFn;

    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @Builder
    public AttachmentInfo(long seq, UsehistSubmitInfo usehistSubmitInfo, String fileNm, String fileExtNm,
                          String filePath, long uploadFn) {
        this.seq = seq;
        this.usehistSubmitInfo = usehistSubmitInfo;
        this.fileNm = fileNm;
        this.fileExtNm = fileExtNm;
        this.filePath = filePath;
        this.uploadFn = uploadFn;
    }
}
