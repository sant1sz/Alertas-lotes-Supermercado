package com.example.alertalote.controller;

import com.example.alertalote.model.Lote;
import com.example.alertalote.repository.LoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lotes")
public class LoteController {

    @Autowired
    private LoteRepository loteRepository;

    // 1. Endpoint para ver todos los lotes en inventario
    @GetMapping
    public List<Lote> listarLotes() {
        return loteRepository.findAll();
    }

    // 2. Endpoint para registrar un nuevo lote (con cantidad, fecha y ubicación)
    @PostMapping
    public Lote guardarLote(@RequestBody Lote lote) {
        return loteRepository.save(lote);
    }
}