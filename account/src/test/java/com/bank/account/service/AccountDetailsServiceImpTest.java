package com.bank.account.service;

import com.bank.account.ParentTest;
import com.bank.account.dto.AccountDetailsDto;
import com.bank.account.entity.AccountDetailsEntity;
import com.bank.account.mapper.AccountDetailsMapperImpl;
import com.bank.account.repository.AccountDetailsRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class AccountDetailsServiceImpTest extends ParentTest {

    private static AccountDetailsEntity accountDetails;

    private static List<AccountDetailsEntity> accountDetailsList;

    @InjectMocks
    private AccountDetailsServiceImp service;

    @Mock
    private AccountDetailsRepository repository;

    @Spy
    private AccountDetailsMapperImpl mapper;

    @BeforeAll
    static void init() {
        accountDetails = getAccountDetails(ONE, ONE, ONE, ONE, BIG_DECIMAL_THREE, Boolean.TRUE, ONE);

        accountDetailsList = getAccountDetailsList(accountDetails);
    }

    @Test
    @DisplayName("чтение позитивный сценарий")
    void readTest() {
        doReturn(Optional.of(accountDetails)).when(repository).findById(anyLong());

        final AccountDetailsDto result = service.readById(ONE);

        assertAll(() -> {
            assertEquals(accountDetails.getId(), result.getId());
            assertEquals(accountDetails.getMoney(), result.getMoney());
            assertEquals(accountDetails.getProfileId(), result.getProfileId());
            assertEquals(accountDetails.getPassportId(), result.getPassportId());
            assertEquals(accountDetails.getAccountNumber(), result.getAccountNumber());
            assertEquals(accountDetails.getBankDetailsId(), result.getBankDetailsId());
            assertEquals(accountDetails.getNegativeBalance(), result.getNegativeBalance());
        });
    }

    @Test
    @DisplayName("чтение негативный сценарий")
    void readNegativeTest() {
        doReturn(Optional.empty()).when(repository).findById(anyLong());

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.readById(ONE)
        );

        assertEquals("AccountDetails с таким id не найдено", exception.getMessage());
    }

    @Test
    @DisplayName("чтение списка позитивный сценарий")
    void readAllTest() {
        doReturn(accountDetailsList).when(repository).findAllById(any());

        final List<AccountDetailsDto> result = service.readAllById(List.of(ONE));

        assertAll( () -> {
            assertEquals(accountDetailsList.size(), result.size());
            assertEquals(getZeroEntityElement(accountDetailsList).getId(), getZeroElement(result).getId());
            assertEquals(getZeroEntityElement(accountDetailsList).getMoney(), getZeroElement(result).getMoney());

            assertEquals(getZeroEntityElement(accountDetailsList).getProfileId(),
                    getZeroElement(result).getProfileId()
            );
            assertEquals(getZeroEntityElement(accountDetailsList).getPassportId(),
                    getZeroElement(result).getPassportId()
            );
            assertEquals(getZeroEntityElement(accountDetailsList).getAccountNumber(),
                    getZeroElement(result).getAccountNumber()
            );
            assertEquals(getZeroEntityElement(accountDetailsList).getBankDetailsId(),
                    getZeroElement(result).getBankDetailsId()
            );
            assertEquals(getZeroEntityElement(accountDetailsList).getNegativeBalance(),
                    getZeroElement(result).getNegativeBalance()
            );
        });
    }

    @Test
    @DisplayName("чтение списка негативный сценарий")
    void readAllNegativeTest() {
        doReturn(List.of(new AccountDetailsEntity())).when(repository).findAllById(any());

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.readAllById(List.of(ONE, TWO))
        );

        assertEquals("Одного или нескольких id из списка не найдено", exception.getMessage());
    }

    @Test
    @DisplayName("создание позитивный сценарий")
    void createTest() {
        doReturn(accountDetails).when(repository).save(any());

        final AccountDetailsDto result = service.create(
                getAccountDetailsDto(ONE, ONE, ONE, ONE, BIG_DECIMAL_THREE, Boolean.TRUE, ONE)
        );

        assertAll(() -> {
            assertEquals(accountDetails.getId(), result.getId());
            assertEquals(accountDetails.getMoney(), result.getMoney());
            assertEquals(accountDetails.getProfileId(), result.getProfileId());
            assertEquals(accountDetails.getPassportId(), result.getPassportId());
            assertEquals(accountDetails.getAccountNumber(), result.getAccountNumber());
            assertEquals(accountDetails.getBankDetailsId(), result.getBankDetailsId());
            assertEquals(accountDetails.getNegativeBalance(), result.getNegativeBalance());
        });
    }

    @Test
    @DisplayName("создание негативный сценарий")
    void createNegativeTest() {
        String massage = "Недопустимые параметры";

        doThrow(new IllegalArgumentException(massage)).when(repository).save(any());

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.create(new AccountDetailsDto())
        );

        assertEquals(massage, exception.getMessage());
    }

    @Test
    @DisplayName("обновление позитивный сценарий")
    void updateTest() {
        doReturn(Optional.of(accountDetails)).when(repository).findById(ONE);
        doReturn(accountDetails).when(repository).save(
                getAccountDetails(ONE, TWO, TWO, TWO, BIG_DECIMAL_THREE, Boolean.FALSE, TWO)
        );

        final AccountDetailsDto result = service.update(
                ONE, getAccountDetailsDto(null, TWO, TWO, TWO, BIG_DECIMAL_THREE, Boolean.FALSE, TWO)
        );

        assertAll(() -> {
            assertEquals(accountDetails.getId(), result.getId());
            assertEquals(accountDetails.getMoney(), result.getMoney());
            assertEquals(accountDetails.getProfileId(), result.getProfileId());
            assertEquals(accountDetails.getPassportId(), result.getPassportId());
            assertEquals(accountDetails.getAccountNumber(), result.getAccountNumber());
            assertEquals(accountDetails.getBankDetailsId(), result.getBankDetailsId());
            assertEquals(accountDetails.getNegativeBalance(), result.getNegativeBalance());
        });
    }

    @Test()
    @DisplayName("обновление с не пустым id и null")
    void updateNullTest() {
        doReturn(Optional.of(accountDetails)).when(repository).findById(ONE);
        doReturn(accountDetails).when(repository).save(any());

        final AccountDetailsDto result = service.update(ONE, null);

        assertAll(() -> {
            assertEquals(accountDetails.getId(), result.getId());
            assertEquals(accountDetails.getMoney(), result.getMoney());
            assertEquals(accountDetails.getProfileId(), result.getProfileId());
            assertEquals(accountDetails.getPassportId(), result.getPassportId());
            assertEquals(accountDetails.getAccountNumber(), result.getAccountNumber());
            assertEquals(accountDetails.getBankDetailsId(), result.getBankDetailsId());
            assertEquals(accountDetails.getNegativeBalance(), result.getNegativeBalance());
        });
    }

    @Test
    @DisplayName("обновление негативный сценарий")
    void updateNegativeTest() {
        doReturn(Optional.empty()).when(repository).findById(anyLong());

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.update(ONE, new AccountDetailsDto()));

        assertEquals("Не существующий id = " + ONE, exception.getMessage());
    }
}
