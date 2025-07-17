package com.ITK.kalenyuk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {
    private String id;
    private String name;
    private String shortName;
}