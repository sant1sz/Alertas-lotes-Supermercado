package com.example.alertalote.model;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Negocio")
public class Negocio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_negocio")
    private Long idNegocio;

    @Column(name = "nombre_tienda", nullable = false)
    private String nombreTienda;

    @Column(name = "telefono_whatsapp", nullable = false)
    private String telefonoWhatsapp;

    // Getters y Setters
    public Long getIdNegocio() { return idNegocio; }
    public void setIdNegocio(Long idNegocio) { this.idNegocio = idNegocio; }

    public String getNombreTienda() { return nombreTienda; }
    public void setNombreTienda(String nombreTienda) { this.nombreTienda = nombreTienda; }

    public String getTelefonoWhatsapp() { return telefonoWhatsapp; }
    public void setTelefonoWhatsapp(String telefonoWhatsapp) { this.telefonoWhatsapp = telefonoWhatsapp; }
}
