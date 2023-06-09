package com.bank.publicinfo.controller;

import com.bank.common.exception.ValidationException;
import com.bank.publicinfo.ParentTest;
import com.bank.publicinfo.dto.LicenseDto;
import com.bank.publicinfo.service.LicenseService;
import com.bank.publicinfo.supplier.LicenseSupplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LicenseController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LicenseControllerTest extends ParentTest {

    private static LicenseDto license;
    private static LicenseSupplier licenseSupplier;

    private final ObjectMapper mapper;
    private final MockMvc mockMvc;

    @MockBean
    private LicenseService service;

    @BeforeAll
    static void init() {
        licenseSupplier = new LicenseSupplier();

        license = licenseSupplier.getDto(ONE, BYTE, null);
    }

    @Test
    @DisplayName("сохранение, позитивный сценарий")
    void savePositiveTest() throws Exception {
        doReturn(license).when(service).save(any());

        final int photo = getIntFromByte(license.getPhoto());

        mockMvc.perform(post("/license")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(license))
        ).andExpectAll(status().isOk(),
                jsonPath("$.photo", is(photo)),
                jsonPath("$.bankDetails", is(license.getBankDetails()))
        );
    }

    @Test
    @DisplayName("сохранение, негативный сценарий")
    void saveNegativeTest() throws Exception {
        doThrow(new ValidationException("Неверные данные")).when(service).save(any());

        mockMvc.perform(post("/license")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(license))
        ).andExpectAll(status().isUnprocessableEntity(),
                content().string("Неверные данные")
        );
    }

    @Test
    @DisplayName("чтение, позитивный сценарий")
    void readPositiveTest() throws Exception {
        doReturn(license).when(service).read(any());

        final int photo = getIntFromByte(license.getPhoto());
        final int id = getIntFromLong(license.getId());

        mockMvc.perform(get("/license/{id}", ONE))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id", is(id)),
                        jsonPath("$.photo", is(photo)),
                        jsonPath("$.bankDetails", is(license.getBankDetails()))

                );
    }

    @Test
    @DisplayName("чтение, негативный сценарий")
    void readNegativeTest() throws Exception {
        doThrow(new EntityNotFoundException("Лицензии нет")).when(service).read(any());

        mockMvc.perform(get("/license/{id}", ONE))
                .andExpectAll(
                        status().isNotFound(),
                        content().string("Лицензии нет")
                );
    }

    @Test
    @DisplayName("чтение по нескольким id, позитивный сценарий")
    void readAllPositiveTest() throws Exception {

        final List<LicenseDto> licenses = returnLicenses();

        doReturn(licenses).when(service).readAll(any());

        final LicenseDto zeroLicense = licenses.get(0);
        final LicenseDto oneLicense = licenses.get(1);

        final int zeroPhoto = getIntFromByte(zeroLicense.getPhoto());
        final int zeroId = getIntFromLong(zeroLicense.getId());

        final int onePhoto = getIntFromByte(oneLicense.getPhoto());
        final int oneId = getIntFromLong(oneLicense.getId());

        mockMvc.perform(get("/license?id=1&id=2")).andExpectAll(status().isOk(),
                jsonPath("$", hasSize(licenses.size())),
                jsonPath("$.[0].id", is(zeroId)),
                jsonPath("$.[0].photo", is(zeroPhoto)),
                jsonPath("$.[0].bankDetails", is(zeroLicense.getBankDetails())),
                jsonPath("$.[1].id", is(oneId)),
                jsonPath("$.[1].photo", is(onePhoto)),
                jsonPath("$.[1].bankDetails", is(oneLicense.getBankDetails()))
        );
    }

    private List<LicenseDto> returnLicenses() {
        return List.of(
                licenseSupplier.getDto(ONE, BYTE, null),
                licenseSupplier.getDto(ONE, BYTE, null)
        );
    }

    @Test
    @DisplayName("чтение по нескольким id, негативный сценарий")
    void readAllNegativeTest() throws Exception {
        doThrow(new EntityNotFoundException("Ошибка в параметрах")).when(service).readAll(any());

        mockMvc.perform(get("/license?id=1")).andExpectAll(status().isNotFound(),
                content().string("Ошибка в параметрах")
        );
    }

    @Test
    @DisplayName("обновление, позитивный сценарий")
    void updatePositiveTest() throws Exception {
        doReturn(license).when(service).update(anyLong(), any());

        final int photo = getIntFromByte(license.getPhoto());
        final int id = getIntFromLong(license.getId());

        mockMvc.perform(put("/license/{id}", ONE).
                contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(license))
        ).andExpectAll(status().isOk(),
                jsonPath("$.id", is(id)),
                jsonPath("$.photo", is(photo)),
                jsonPath("$.bankDetails", is(license.getBankDetails()))
        );
    }

    @Test
    @DisplayName("обновление, негативный сценарий")
    void updateNegativeTest() throws Exception {
        doThrow(new EntityNotFoundException("Обновление невозможно")).when(service).update(anyLong(), any());

        mockMvc.perform(put("/license/{id}", ONE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(license))
        ).andExpectAll(status().isNotFound(),
                content().string("Обновление невозможно")
        );
    }
}
