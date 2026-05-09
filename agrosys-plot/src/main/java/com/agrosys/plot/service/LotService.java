package com.agrosys.plot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.agrosys.plot.dto.Response;
import com.agrosys.plot.dto.lot.ConfigRegister;
import com.agrosys.plot.repository.LotRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LotService {

    private final LotRepository lotRepository;

    public Response getAllConfigs() {
        List<LotRepository.ConfigsProjection> configList = lotRepository.findAllConfigs();
        return Response.builder()
                .message("Configuraciones obtenidas correctamente")
                .success(true)
                .data(configList)
                .build();
    }

    // ======================== TRADE MODE ========================
    public Response postTradeMode(ConfigRegister request) {
        Integer find = lotRepository.findTradeModeByName(request.getName());

        if (find != null) {
            throw new IllegalArgumentException(
                    "El nombre " + request.getName() + " para Modo de comercio ya esta en uso");
        }

        Integer insert = lotRepository.insertTradeMode(request.getName(), request.getActive());

        if (insert <= 0) {
            throw new RuntimeException("Error al registrar el modo de comercio.");
        }

        return Response.builder()
                .message("Modo de comercio registrado exitosamente!")
                .success(true)
                .build();
    }

    public Response putTradeMode(Integer id, ConfigRegister request) {
        Integer find = lotRepository.findTradeModeByName(id, request.getName());

        if (find != null) {
            throw new IllegalArgumentException(
                    "El nombre " + request.getName() + " para Modo de comercio ya esta en uso");
        }

        Integer update = lotRepository.updateTradeMode(id, request.getName(), request.getActive());

        if (update <= 0) {
            throw new RuntimeException("Error al actualizar el modo de comercio.");
        }

        return Response.builder()
                .message("Modo de comercio actualizado exitosamente!")
                .success(true)
                .build();
    }

    // ======================== PRODUCT UNIT ========================
    public Response postProductUnit(ConfigRegister request) {
        Integer find = lotRepository.findProductUnitByName(request.getName());

        if (find != null) {
            throw new IllegalArgumentException(
                    "El nombre " + request.getName() + " para Unidad de producto ya esta en uso");
        }

        Integer insert = lotRepository.insertProductUnit(request.getName(), request.getActive());

        if (insert <= 0) {
            throw new RuntimeException("Error al registrar la unidad de producto.");
        }

        return Response.builder()
                .message("Unidad de producto registrada exitosamente!")
                .success(true)
                .build();
    }

    public Response putProductUnit(Integer id, ConfigRegister request) {
        Integer find = lotRepository.findProductUnitByName(id, request.getName());

        if (find != null) {
            throw new IllegalArgumentException(
                    "El nombre " + request.getName() + " para Unidad de producto ya esta en uso");
        }

        Integer update = lotRepository.updateProductUnit(id, request.getName(), request.getActive());

        if (update <= 0) {
            throw new RuntimeException("Error al actualizar la unidad de producto.");
        }

        return Response.builder()
                .message("Unidad de producto actualizada exitosamente!")
                .success(true)
                .build();
    }

    // ======================== PRODUCT TYPE ========================
    public Response postProductType(ConfigRegister request) {
        Integer find = lotRepository.findProductTypeByName(request.getName());

        if (find != null) {
            throw new IllegalArgumentException(
                    "El nombre " + request.getName() + " para Tipo de producto ya esta en uso");
        }

        Integer insert = lotRepository.insertProductType(request.getName(), request.getActive());

        if (insert <= 0) {
            throw new RuntimeException("Error al registrar el tipo de producto.");
        }

        return Response.builder()
                .message("Tipo de producto registrado exitosamente!")
                .success(true)
                .build();
    }

    public Response putProductType(Integer id, ConfigRegister request) {
        Integer find = lotRepository.findProductTypeByName(id, request.getName());

        if (find != null) {
            throw new IllegalArgumentException(
                    "El nombre " + request.getName() + " para Tipo de producto ya esta en uso");
        }

        Integer update = lotRepository.updateProductType(id, request.getName(), request.getActive());

        if (update <= 0) {
            throw new RuntimeException("Error al actualizar el tipo de producto.");
        }

        return Response.builder()
                .message("Tipo de producto actualizado exitosamente!")
                .success(true)
                .build();
    }

}
