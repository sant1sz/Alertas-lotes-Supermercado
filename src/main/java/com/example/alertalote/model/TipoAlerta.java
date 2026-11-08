package com.example.alertalote.model;

public enum TipoAlerta {
    /** El lote ya pasó su fecha de vencimiento y aún tiene unidades. */
    VENCIDO,
    /** El lote vence dentro de los días de anticipación configurados. */
    PROXIMO_A_VENCER
}
