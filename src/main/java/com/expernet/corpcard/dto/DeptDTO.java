package com.expernet.corpcard.dto;

import com.expernet.corpcard.entity.Dept;
import com.expernet.corpcard.entity.UserAddInfo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DeptDTO {
    private long seq;
    private String deptNm;
    private String deptCd;
    private String upperDeptCd;
    private String chiefTitle;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private DeptDTO upper;
    private final List<DeptDTO> lower = new ArrayList<>();
}
