package com.inditex.gym_lorza.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String dni;
    private int start_year;
    private boolean activo;
    private String image;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public int getStart_year() { return start_year; }
    public void setStart_year(int start_year) { this.start_year = start_year; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}