package com.example.alertalote.model;
import jakarta.persistence.*;

@Entity
@Table(name = "Producto")

public class Producto {
    @Id
    @Column(name = "codigo_barras")
    private String codigoBarras;

    @ManyToOne
    @JoinColumn(name = "id_negocio")
    private Negocio negocio;

    @Column(nullable = false)
    private String nombre;

    private String categoria;

    // Getters y Setters
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public Negocio getNegocio() { return negocio; }
    public void setNegocio(Negocio negocio) { this.negocio = negocio; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}
