package com.demo.ticketing.utils;

import com.demo.ticketing.dto.PisteAuditDto;
import com.demo.ticketing.model.PisteAudit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PisteAuditMapper {

    public static PisteAudit convertPisteAuditDtoToPisteAudit(PisteAuditDto pisteAuditDto){

        return new PisteAudit();
    }

    public static PisteAuditDto convertPisteAuditToPisteAuditDto(PisteAudit pisteAudit){

        return new PisteAuditDto();
    }

}
