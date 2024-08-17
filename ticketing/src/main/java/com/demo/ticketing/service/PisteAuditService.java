package com.demo.ticketing.service;

import com.demo.ticketing.dto.PisteAuditDto;
import com.demo.ticketing.model.PisteAudit;

public interface PisteAuditService extends MapperService<PisteAudit, PisteAuditDto>{
    void savePisteAudit(PisteAudit pisteAudit);
}
