package com.demo.ticketing.service;

import com.demo.ticketing.dto.PisteAuditDto;
import com.demo.ticketing.model.Action;
import com.demo.ticketing.model.PisteAudit;
import com.demo.ticketing.model.Ticket;
import com.demo.ticketing.model.User;
import com.demo.ticketing.repository.PisteAuditRepository;
import com.demo.ticketing.service.Impl.PisteAuditServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PisteAuditServiceTest {

    @Mock
    private PisteAuditRepository pisteAuditRepository;

    @Mock
    private UserService userService;

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private PisteAuditServiceImpl pisteAuditService;

    @Test
    @DisplayName("Tester la fonction savePisteAudit")
    public void testSavePisteAudit() {

        PisteAudit pisteAudit = new PisteAudit();
        pisteAudit.setId("15dd7eac-e0fb-4f63-8316-a8bec10be73f");
        pisteAudit.setAction(Action.CREATE);

        when(pisteAuditRepository.save(pisteAudit)).thenReturn(pisteAudit);

        pisteAuditService.savePisteAudit(pisteAudit);

        assertNotNull(pisteAudit);
        assertEquals("15dd7eac-e0fb-4f63-8316-a8bec10be73f",pisteAudit.getId());
        assertEquals(Action.CREATE,pisteAudit.getAction());

        verify(pisteAuditRepository).save(pisteAudit);
    }

    @Test
    @DisplayName("Tester la fonction mapToEntity si le dto est valide")
    public void testMapToEntity_ValidDto() {
        PisteAuditDto dto = new PisteAuditDto();
        dto.setUserId("15dd7eac-e0fb-4f63-8316-a8bec10be73f");
        dto.setTicketId("9a32d97e-9eba-460d-8cb0-9b789f648b18");
        dto.setAction(Action.CREATE);

        User user = new User();
        user.setId("15dd7eac-e0fb-4f63-8316-a8bec10be73f");

        Ticket ticket = new Ticket();
        ticket.setId("9a32d97e-9eba-460d-8cb0-9b789f648b18");

        when(userService.getUserByIdForPisteAudit("15dd7eac-e0fb-4f63-8316-a8bec10be73f")).thenReturn(Optional.of(user));
        when(ticketService.getTicketByIdForPisteAudit("9a32d97e-9eba-460d-8cb0-9b789f648b18")).thenReturn(Optional.of(ticket));

        PisteAudit pisteAudit = pisteAuditService.mapToEntity(dto);

        assertNotNull(pisteAudit);
        assertEquals("15dd7eac-e0fb-4f63-8316-a8bec10be73f", pisteAudit.getUser().getId());
        assertEquals("9a32d97e-9eba-460d-8cb0-9b789f648b18", pisteAudit.getTicket().getId());
        assertEquals(Action.CREATE, pisteAudit.getAction());
    }

    @Test
    @DisplayName("Tester la fonction mapToEntity si le dto est invalide")
    public void testMapToEntity_InvalidDto() {
        PisteAuditDto dto = new PisteAuditDto();
        dto.setUserId("15dd7eac-e0fb-4f63-8316-a8bec10be73f");
        dto.setTicketId("9a32d97e-9eba-460d-8cb0-9b789f648b18");

        when(userService.getUserByIdForPisteAudit("15dd7eac-e0fb-4f63-8316-a8bec10be73f")).thenReturn(Optional.empty());
        when(ticketService.getTicketByIdForPisteAudit("9a32d97e-9eba-460d-8cb0-9b789f648b18")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pisteAuditService.mapToEntity(dto);
        });

        assertEquals("la piste audit dto est invalide", exception.getMessage());
    }

    @Test
    @DisplayName("Tester la fonction mapToDto")
    public void testMapToDto() {
        User user = new User();
        user.setId("15dd7eac-e0fb-4f63-8316-a8bec10be73f");

        Ticket ticket = new Ticket();
        ticket.setId("9a32d97e-9eba-460d-8cb0-9b789f648b18");

        PisteAudit pisteAudit = new PisteAudit();
        pisteAudit.setId("123");
        pisteAudit.setUser(user);
        pisteAudit.setTicket(ticket);
        pisteAudit.setAction(Action.CREATE);
        pisteAudit.setUserConcerned("ConcernedUser");
        pisteAudit.setLastUpdate(LocalDateTime.now());

        PisteAuditDto dto = pisteAuditService.mapToDto(pisteAudit);

        assertNotNull(dto);
        assertEquals("123", dto.getPisteAuditId());
        assertEquals("15dd7eac-e0fb-4f63-8316-a8bec10be73f", dto.getUserId());
        assertEquals("9a32d97e-9eba-460d-8cb0-9b789f648b18", dto.getTicketId());
        assertEquals("ConcernedUser", dto.getUserConcerned());
        assertEquals(Action.CREATE, dto.getAction());
    }

}
