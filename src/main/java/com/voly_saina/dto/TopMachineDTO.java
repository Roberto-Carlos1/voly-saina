package com.voly_saina.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopMachineDTO {
    private Long idMachine;
    private String nomMachine;
    private String typeMachine;
    private int nombreLocations;
    private BigDecimal totalDepense;
}
