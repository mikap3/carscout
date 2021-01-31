package com.udacity.vehicles.domain.manufacturer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Declares class to hold car manufacturer information.
 */
@Entity
@Getter @Setter @NoArgsConstructor
public class Manufacturer {

    @Id
    private Integer code;

    private String name;

    public Manufacturer(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
