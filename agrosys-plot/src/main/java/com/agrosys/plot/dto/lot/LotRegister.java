package com.agrosys.plot.dto.lot;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LotRegister {
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @Positive(message = "Es necesario elegir una plantación.")
    @Min(value = 1)
    private Integer plantationId;

    @Positive(message = "Es necesario elegir un tipo de prducto.")
    @Min(value = 1)
    private Integer productType;

    @Positive(message = "Es necesario elegir una unidad de producto.")
    @Min(value = 1)
    private Integer unitType;

    @Positive(message = "Es necesario ingresar un costo unitario.")
    @Min(value = 0)
    private BigDecimal unitCost;

    @Positive(message = "Es necesario elegir una modo de trato.")
    @Min(value = 1)
    private Integer tradeMode;

    private BigDecimal freightCost;

    @Size(max = 255, message = "Las notas deben tener maximo 255 caracteres")
    private String description;
}
