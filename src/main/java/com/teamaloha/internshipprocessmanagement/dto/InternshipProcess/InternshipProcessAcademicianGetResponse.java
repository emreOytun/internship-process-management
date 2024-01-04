package com.teamaloha.internshipprocessmanagement.dto.InternshipProcess;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternshipProcessAcademicianGetResponse extends InternshipProcessGetResponse {
    @JsonProperty
    private String commentOwner;
}
