package com.agrosys.plot.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.agrosys.plot.dto.Response;
import com.agrosys.plot.dto.lot.ConfigRegister;
import com.agrosys.plot.dto.lot.LotRegister;
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

    // ======================== LOTS OF PLANTATION ========================

    public Response getAllPlantations() {
        List<LotRepository.SelectPlantationsProjection> plantations = lotRepository.findAllPlantations();
        return Response.builder()
                .message("Plantaciones obtenidas correctamente")
                .success(true)
                .data(plantations)
                .build();
    }

    public Response postLot(LotRegister request) {
        Integer find = lotRepository.findLotByName(request.getName(), request.getPlantationId());
        log.info("request: {}", request);
        if (find != null) {
            throw new IllegalArgumentException(
                    "El nombre " + request.getName() + "del lote ya esta en uso dentro de la plantación");
        }

        Integer insert = lotRepository.insertLot(request.getName(), request.getPlantationId(), request.getDescription());
        if (insert != 1) {
            throw new RuntimeException("Error al crear nuevo lote.");
        }

        Integer newLotId = lotRepository.findLotByName(request.getName(), request.getPlantationId());

        Integer lotTrade = lotRepository.insertLotTrade(newLotId, request.getProductType(), request.getUnitType(),
                request.getUnitCost(), request.getTradeMode(), request.getFreightCost());

        if (lotTrade != 1) {
            throw new RuntimeException("Error al crear tipo de trato del nuevo lote.");
        }

        return Response.builder()
                .message("Lote registrado exitosamente!")
                .success(true)
                .build();
    }

    public Response getLotsByPlantation(Integer id) {
        List<LotRepository.LotsByPlantationsProjection> plantations = lotRepository.findAllLotsByPlantation(id);
        return Response.builder()
                .message("Lotes de plantacion obtenidas correctamente")
                .success(true)
                .data(plantations)
                .build();
    }

    public Response updateLot(Integer id, Integer lotTradeId, LotRegister request) {
        Integer find = lotRepository.findLotByName(request.getName(), request.getPlantationId(), id);
        if (find != null) {
            throw new IllegalArgumentException(
                    "El nombre " + request.getName() + "del lote ya esta en uso dentro de la plantación");
        }

        Integer update = lotRepository.updateLot(id, request.getName(), request.getDescription());
        if (update != 1) {
            throw new RuntimeException("Error al actualizar el lote.");
        }

        Integer updateLotTrade = lotRepository.updateLotTrade(lotTradeId, request.getProductType(), request.getUnitType(),
                request.getUnitCost(), request.getTradeMode(), request.getFreightCost());

        if (updateLotTrade != 1) {
            throw new RuntimeException("Error al actualizar el tipo de trato del lote.");
        }

        return Response.builder()
                .message("Lote actualizado exitosamente!")
                .success(true)
                .build();
    }

    public Response deleteLot(Integer id, Integer lotTradeId) {
        Integer delete = lotRepository.deleteLot(id);
        if (delete != 1) {
            throw new RuntimeException("Error al eliminar el lote.");
        }

        // Integer deleteLotTrade = lotRepository.deleteLotTrade(lotTradeId);

        // if (deleteLotTrade != 1) {
        //     throw new RuntimeException("Error al eliminar el tipo de trato del lote.");
        // }

        return Response.builder()
                .message("Lote eliminado exitosamente!")
                .success(true)
                .build();
    }

}
