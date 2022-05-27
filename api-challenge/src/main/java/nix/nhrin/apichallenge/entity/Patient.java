package nix.nhrin.apichallenge.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Patient {
    private LocalDate birthDate;
    private String zipCode;
    private LocalDate admissionDate;
    private LocalDate dischargeDate;
    private String notes;
}
