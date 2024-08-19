package com.demo.ticketing.service;

import com.demo.ticketing.dto.TicketDto;
import com.demo.ticketing.model.Ticket;
import com.demo.ticketing.model.User;
import com.demo.ticketing.repository.TicketRepository;
import com.demo.ticketing.service.Impl.TicketServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @Mock
    private UserService userService;


    @Test
    @DisplayName("Tester la fonction getTicketById")
    public void testGetTicketById() {
        String ticketId = "9a32d97e-9eba-460d-8cb0-9b789f648b18";
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);

        when(ticketRepository.findByIdAndDeleted(ticketId, false)).thenReturn(Optional.of(ticket));

        Optional<Ticket> result = ticketService.getTicketById(ticketId);

        assertTrue(result.isPresent());
        assertEquals(ticketId, result.get().getId());
    }

    @Test
    @DisplayName("Tester la fonction getTicketByIdForPisteAudit")
    public void testGetTicketByIdForPisteAudit() {
        String ticketId = "9a32d97e-9eba-460d-8cb0-9b789f648b18";
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        Optional<Ticket> result = ticketService.getTicketByIdForPisteAudit(ticketId);

        assertTrue(result.isPresent());
        assertEquals(ticketId, result.get().getId());
    }

    @Test
    @DisplayName("Tester la fonction getAllTicketsByUser")
    public void testGetAllTicketsByUser() {
        User user = new User();
        user.setId("be13d331-976d-47ff-bc59-4f0e495e1928");
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(new Ticket());

        when(ticketRepository.findByUserAndDeleted(user, false)).thenReturn(tickets);

        List<Ticket> result = ticketService.getAllTicketsByUser(user);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Tester la fonction getTicketByUserAndId")
    public void testGetTicketByUserAndId() {
        String ticketId = "9a32d97e-9eba-460d-8cb0-9b789f648b18";
        User user = new User();
        user.setId("be13d331-976d-47ff-bc59-4f0e495e1928");
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);

        when(ticketRepository.findByUserAndIdAndDeleted(user, ticketId, false)).thenReturn(Optional.of(ticket));

        Optional<Ticket> result = ticketService.getTicketByUserAndId(user, ticketId);

        assertTrue(result.isPresent());
        assertEquals(ticketId, result.get().getId());
    }

    @Test
    @DisplayName("Tester la fonction saveTicket si on a un nouveau ticket")
    public void testSaveTicket_NewTicket() {
        Ticket ticket = new Ticket();
        ticket.setTitle("New Ticket");

        when(ticketRepository.save(ticket)).thenReturn(ticket);

        Ticket result = ticketService.saveTicket(ticket);

        assertNotNull(result.getId());
        assertFalse(result.isDeleted());
        verify(ticketRepository).save(ticket);
    }

    @Test
    @DisplayName("Tester la fonction saveTicket si pour un ticket existant")
    public void testSaveTicket_ExistingTicket() {
        Ticket ticket = new Ticket();
        ticket.setId("9a32d97e-9eba-460d-8cb0-9b789f648b18");
        ticket.setTitle("Existing Ticket");

        when(ticketRepository.save(ticket)).thenReturn(ticket);

        Ticket result = ticketService.saveTicket(ticket);

        assertEquals("9a32d97e-9eba-460d-8cb0-9b789f648b18", result.getId());
        verify(ticketRepository).save(ticket);
    }

    @Test
    @DisplayName("Tester la fonction deleteTicket")
    public void testDeleteTicket() {
        Ticket ticket = new Ticket();
        ticket.setId("9a32d97e-9eba-460d-8cb0-9b789f648b18");

        when(ticketRepository.save(ticket)).thenReturn(ticket);

        ticketService.deleteTicket(ticket);

        assertTrue(ticket.isDeleted());
        verify(ticketRepository).save(ticket);
    }

    @Test
    @DisplayName("Tester la fonction mapToEntity si le dto est valide")
    public void testMapToEntity_ValidDto() {
        TicketDto dto = new TicketDto();
        dto.setTicketId("9a32d97e-9eba-460d-8cb0-9b789f648b18");
        dto.setUserId("be13d331-976d-47ff-bc59-4f0e495e1928");
        dto.setTitle("Ticket Title");

        User user = new User();
        user.setId("be13d331-976d-47ff-bc59-4f0e495e1928");

        when(userService.getUserById("be13d331-976d-47ff-bc59-4f0e495e1928")).thenReturn(Optional.of(user));

        Ticket result = ticketService.mapToEntity(dto);

        assertNotNull(result);
        assertEquals("9a32d97e-9eba-460d-8cb0-9b789f648b18", result.getId());
        assertEquals("be13d331-976d-47ff-bc59-4f0e495e1928", result.getUser().getId());
        assertEquals("Ticket Title", result.getTitle());
    }

    @Test
    @DisplayName("Tester la fonction mapToEntity si le dto est invalide")
    public void testMapToEntity_InvalidDto() {
        TicketDto dto = new TicketDto();
        dto.setTicketId("9a32d97e-9eba-460d-8cb0-9b789f648b18");
        dto.setUserId("1e13d331-976o-47ff-bc59-4f0e495e1828");

        when(userService.getUserById("1e13d331-976o-47ff-bc59-4f0e495e1828")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ticketService.mapToEntity(dto);
        });

        assertEquals("le ticket dto est invalide", exception.getMessage());
    }

    @Test
    @DisplayName("Tester la fonction mapToDto")
    public void testMapToDto() {
        User user = new User();
        user.setId("be13d331-976d-47ff-bc59-4f0e495e1928");

        Ticket ticket = new Ticket();
        ticket.setId("9a32d97e-9eba-460d-8cb0-9b789f648b18");
        ticket.setUser(user);
        ticket.setTitle("Ticket Title");

        TicketDto result = ticketService.mapToDto(ticket);

        assertNotNull(result);
        assertEquals("9a32d97e-9eba-460d-8cb0-9b789f648b18", result.getTicketId());
        assertEquals("be13d331-976d-47ff-bc59-4f0e495e1928", result.getUserId());
        assertEquals("Ticket Title", result.getTitle());
    }

}
