package com.teamaloha.internshipprocessmanagement.dto.InternshipProcess;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class InternshipProcessAcademicianGetResponse extends InternshipProcessGetResponse {
    @JsonProperty
    private String commentOwner;
}
