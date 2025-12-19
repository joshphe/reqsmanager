package com.example.reqsmanager.dto;
import lombok.Data;
import java.util.List;
@Data
public class TeamForFormDTO {
    private Integer id;
    private String name;
    private List<GroupForFormDTO> groups;
}