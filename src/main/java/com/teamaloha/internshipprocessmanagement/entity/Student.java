package com.teamaloha.internshipprocessmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "student")
@Data
public class Student extends User {
}
