package com.agrosys.plot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.agrosys.plot.dto.Response;
import com.agrosys.plot.dto.plot.PlotRegister;
import com.agrosys.plot.repository.PlotRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlotService {
    
    private final PlotRepository plotRepository;

    public List<PlotRepository.PlotProjection> getAllRoles() {
        log.info("Obteniendo parcelas");
        return plotRepository.findAllPlots();
    }

    @Transactional
    public Response postPlot(PlotRegister request) {

        Integer rol = plotRepository.existsByName(request.getName());
        if (rol != null) {
            throw new IllegalArgumentException("El nombre de la parcela ya existe");
        }

        Integer rolId = plotRepository.insertPlot(request.getName(), request.getDescription());

        if (rolId.equals(0)) {
            throw new RuntimeException("No se pudo registrar la parcela");
        }

        return Response.builder()
                .message("Parcela registrada exitosamente")
                .success(true)
                .build();
    }

    @Transactional
    public Response updatePlot(Integer plotId, PlotRegister request) {

        Integer plot = plotRepository.existsByName(request.getName(), plotId);
        if (plot != null) {
            throw new IllegalArgumentException("El nombre de la parcela ya esta en uso...");
        }

        Integer insert = plotRepository.updatePlot(plotId, request.getName(), request.getDescription());

        if (insert.equals(0)) {
            throw new RuntimeException("No se pudo actualizar la parcela");
        }

        return Response.builder()
                .message("Parcela Actualizada exitosamente")
                .success(true)
                .build();
    }
}
