package com.ITK.kalenyuk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Issue {
    private String id;
    private String summary;
    private String description;
    private Project project;
}