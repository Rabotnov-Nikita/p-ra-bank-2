package com.bank.publicinfo.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AtmDto {
    Long id;
    String address;
    LocalTime startOfWork;
    LocalTime endOfWork;
    Boolean allHours;
    BranchDto branch;
}
