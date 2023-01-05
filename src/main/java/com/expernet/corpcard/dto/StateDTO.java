package com.expernet.corpcard.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class StateDTO {
    @NotNull
    private final String stateCd;
    private final Long submitSeq;
    private final String writerId;
    private final String checkerId;
    @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])$")
    private final String wrtYm;
}
