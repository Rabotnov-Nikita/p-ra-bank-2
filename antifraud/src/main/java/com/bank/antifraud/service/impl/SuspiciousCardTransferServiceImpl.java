package com.bank.antifraud.service.impl;

import com.bank.antifraud.dto.SuspiciousCardTransferDto;
import com.bank.antifraud.entity.SuspiciousCardTransferEntity;
import com.bank.antifraud.mapper.SuspiciousCardTransferMapper;
import com.bank.antifraud.repository.SuspiciousCardTransferRepository;
import com.bank.antifraud.service.SuspiciousCardTransferService;
import com.bank.antifraud.util.ListSizeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

/**
 * Реализация {@link SuspiciousCardTransferService}
 */
@Service
@RequiredArgsConstructor
public class SuspiciousCardTransferServiceImpl implements SuspiciousCardTransferService {

    private final SuspiciousCardTransferRepository repository;
    private final SuspiciousCardTransferMapper mapper;
    private final ListSizeValidator listSizeValidator;


    /**
     * @param transfer {@link SuspiciousCardTransferDto}
     * @return {@link SuspiciousCardTransferDto}
     */
    @Override
    @Transactional
    public SuspiciousCardTransferDto create(SuspiciousCardTransferDto transfer) {
        final SuspiciousCardTransferEntity suspiciousCardTransfer = repository.save(
                mapper.toEntity(transfer)
        );
        return mapper.toDto(suspiciousCardTransfer);
    }

    /**
     * @param id технический идентификатор {@link SuspiciousCardTransferEntity}
     * @return {@link SuspiciousCardTransferDto}
     */
    @Override
    public SuspiciousCardTransferDto read(Long id) {
        final SuspiciousCardTransferEntity suspiciousCardTransfer = findById(id);
        return mapper.toDto(suspiciousCardTransfer);
    }

    /**
     * @param ids список технических идентификаторов {@link SuspiciousCardTransferEntity}
     * @return {@link SuspiciousCardTransferDto}
     */
    @Override
    public List<SuspiciousCardTransferDto> readAll(List<Long> ids) {
        final List<SuspiciousCardTransferEntity> suspiciousCardTransfers = repository.findAllById(ids);
        listSizeValidator.throwIfDifferent(ids, suspiciousCardTransfers,
                () -> new EntityNotFoundException("Количество найденных и запрошенных записей не совпадает.")
        );
        return mapper.toListDto(suspiciousCardTransfers);
    }

    /**
     * @param transfer {@link SuspiciousCardTransferDto}
     * @param id технический идентификатор {@link SuspiciousCardTransferEntity}
     * @return {@link SuspiciousCardTransferDto}
     */
    @Override
    @Transactional
    public SuspiciousCardTransferDto update(SuspiciousCardTransferDto transfer, Long id) {
        final SuspiciousCardTransferEntity suspiciousCardTransferById = findById(id);
        final SuspiciousCardTransferEntity savedSuspiciousCardTransfer = repository.save(
                mapper.mergeToEntity(transfer, suspiciousCardTransferById)
        );
        return mapper.toDto(savedSuspiciousCardTransfer);
    }

    /**
     * @param id технический идентификатор {@link SuspiciousCardTransferEntity}
     * @return {@link SuspiciousCardTransferEntity}
     */
    private SuspiciousCardTransferEntity findById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("SuspiciousCardTransfer с id = " + id + " не найден.")
        );
    }
}
