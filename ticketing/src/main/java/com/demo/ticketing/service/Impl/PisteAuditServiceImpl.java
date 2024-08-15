package com.demo.ticketing.service.Impl;

import com.demo.ticketing.model.PisteAudit;
import com.demo.ticketing.repository.PisteAuditRepository;
import com.demo.ticketing.service.PisteAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PisteAuditServiceImpl implements PisteAuditService {

    private final PisteAuditRepository pisteAuditRepository;

    @Override
    public void savePisteAudit(PisteAudit pisteAudit) {
        log.info("piste audit {}",pisteAudit);
        pisteAuditRepository.save(pisteAudit);
    }

}
