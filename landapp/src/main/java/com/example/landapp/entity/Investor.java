package com.example.landapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "investors")
@Data
@EqualsAndHashCode(callSuper = true)
<<<<<<< HEAD
=======
@NoArgsConstructor
>>>>>>> bec3dae (backend error)
public class Investor extends BaseUser {

    // Inherits id, firstName, and lastName from BaseUser!
    private String email;
    private String nationality;


}