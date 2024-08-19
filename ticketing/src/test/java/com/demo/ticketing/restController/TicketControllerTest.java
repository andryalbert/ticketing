package com.demo.ticketing.restController;

import com.demo.ticketing.dto.TicketDto;
import com.demo.ticketing.model.Action;
import com.demo.ticketing.model.PisteAudit;
import com.demo.ticketing.model.Ticket;
import com.demo.ticketing.model.User;
import com.demo.ticketing.service.PisteAuditService;
import com.demo.ticketing.service.TicketService;
import com.demo.ticketing.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(TicketController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    @MockBean
    private UserService userService;

    @MockBean
    private PisteAuditService pisteAuditService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ParameterizedTest
    @MethodSource("getVariables")
    @DisplayName("Tester la fonction qui récupére tous les tickets")
    @WithMockUser(username = "test",password = "1234Test$", roles = "")
    public void testGetAllTickets(List<Ticket> tickets, List<TicketDto> ticketDtos) throws Exception {

        // Récupérer l'utilisateur connecté depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = new User();
        user.setUserName(username);

        Ticket ticket1 = tickets.get(0);
        Ticket ticket2 = tickets.get(1);

        TicketDto ticketDto1 = ticketDtos.get(0);
        TicketDto ticketDto2 = ticketDtos.get(1);

        when(userService.getUserByUserName(user.getUsername())).thenReturn(user);
        when(ticketService.getAllTicketsByUser(user)).thenReturn(tickets);
        when(ticketService.mapToDto(ticket1)).thenReturn(ticketDto1);
        when(ticketService.mapToDto(ticket2)).thenReturn(ticketDto2);

        mockMvc.perform(get("/tickets").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(ticketDtos)
                ));

        verify(userService).getUserByUserName(user.getUsername());
        verify(ticketService).getAllTicketsByUser(user);
        verify(ticketService).mapToDto(ticket1);
        verify(ticketService).mapToDto(ticket2);

    }

    @ParameterizedTest
    @MethodSource("getVariables")
    @DisplayName("Tester la fonction qui récupére un ticket par son ID si le ticket exist")
    @WithMockUser(username = "test",password = "1234Test$", roles = "")
    public void testGetTicketById_TicketExists(List<Ticket> tickets, List<TicketDto> ticketDtos) throws Exception {

        // Récupérer l'utilisateur connecté depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = new User();
        user.setUserName(username);

        String ticketId = "9a32d97e-9eba-460d-8cb0-9b789f648b18";

        Ticket ticket = tickets.get(0);

        TicketDto ticketDto = ticketDtos.get(0);
        ticketDto.setTicketId(ticketId);

        when(userService.getUserByUserName(user.getUsername())).thenReturn(user);
        when(ticketService.getTicketByUserAndId(user, ticketId)).thenReturn(Optional.of(ticket));
        when(ticketService.mapToDto(ticket)).thenReturn(ticketDto);

        mockMvc.perform(get("/tickets/{id}", ticketId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(ticketDto)
                ));

        verify(userService).getUserByUserName(user.getUsername());
        verify(ticketService).getTicketByUserAndId(user, ticketId);
        verify(ticketService).mapToDto(ticket);
    }

    @Test
    @DisplayName("Tester la fonction qui récupére un ticket par son ID si le ticket n'exist pas")
    @WithMockUser(username = "test",password = "1234Test$", roles = "")
    public void testGetTicketById_TicketNotExists() throws Exception {

        // Récupérer l'utilisateur connecté depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = new User();
        user.setUserName(username);

        String ticketId = "2a32d97e-1eba-460d-8db0-9b789f648b18";

        TicketDto emptyTicketDto = new TicketDto();

        when(userService.getUserByUserName(user.getUsername())).thenReturn(user);
        when(ticketService.getTicketByUserAndId(user, ticketId)).thenReturn(Optional.empty());
        when(ticketService.mapToDto(any(Ticket.class))).thenReturn(emptyTicketDto);

        mockMvc.perform(get("/tickets/{id}", ticketId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(emptyTicketDto)
                ));

        verify(userService).getUserByUserName(user.getUsername());
        verify(ticketService).getTicketByUserAndId(user, ticketId);
        verify(ticketService).mapToDto(any(Ticket.class));

    }

    @Test
    @DisplayName("Tester la fonction qui crée un nouveau ticket")
    @WithMockUser(username = "test",password = "1234Test$", roles = "")
    public void testCreateTicket() throws Exception {

        // Récupérer l'utilisateur connecté depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = new User();
        user.setUserName(username);

        TicketDto ticketDto = new TicketDto();
        ticketDto.setTitle("test get ticket");
        ticketDto.setDescription("Description du ticket");

        Ticket createdTicket = new Ticket();
        createdTicket.setTitle("test get ticket");
        createdTicket.setDescription("Description du ticket");
        createdTicket.setUser(user);

        PisteAudit pisteAudit = new PisteAudit();
        pisteAudit.setId("12dd7eac-e0fb-4f63-8316-a8bec10be73f");
        pisteAudit.setAction(Action.CREATE);
        pisteAudit.setUserConcerned(user.getUsername());
        pisteAudit.setDeleted(false);
        pisteAudit.setLastUpdate(LocalDateTime.now());
        pisteAudit.setTicket(createdTicket);
        pisteAudit.setUser(null);

        when(userService.getUserByUserName(user.getUsername())).thenReturn(user);
        when(ticketService.mapToEntity(any(TicketDto.class))).thenReturn(createdTicket);
        when(ticketService.saveTicket(any(Ticket.class))).thenReturn(createdTicket);
        when(ticketService.mapToDto(any(Ticket.class))).thenReturn(ticketDto);
        doNothing().when(pisteAuditService).savePisteAudit(any(PisteAudit.class));

        mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("test get ticket"))
                .andExpect(content().json(
                        objectMapper.writeValueAsString(ticketDto)
                ));

        verify(userService).getUserByUserName(user.getUsername());
        verify(ticketService).mapToEntity(ticketDto);
        verify(ticketService).saveTicket(createdTicket);
        verify(ticketService).mapToDto(createdTicket);
        verify(pisteAuditService).savePisteAudit(any(PisteAudit.class));

    }

    @Test
    @DisplayName("Tester la fonction qui met à jour un ticket existant si le ticket existe")
    @WithMockUser(username = "test",password = "1234Test$", roles = "")
    public void testUpdateTicket_TicketExists() throws Exception {

        // Récupérer l'utilisateur connecté depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = new User();
        user.setUserName(username);

        String ticketId = "9a32d97e-9eba-460d-8cb0-9b789f648b18";

        Ticket existingTicket = new Ticket();
        existingTicket.setId(ticketId);
        existingTicket.setUser(user);
        existingTicket.setTitle("Existed title");
        existingTicket.setDescription("This is the existed description");

        TicketDto ticketDto = new TicketDto();
        ticketDto.setTitle("Updated Ticket Title");
        ticketDto.setDescription("This is the updated description");

        Ticket updatedTicket = new Ticket();
        updatedTicket.setId(ticketId);
        updatedTicket.setTitle("Updated Ticket Title");
        updatedTicket.setDescription("This is the updated description");
        updatedTicket.setUser(user);

        TicketDto updatedTicketDto = new TicketDto();
        updatedTicketDto.setTicketId(ticketId);
        updatedTicketDto.setTitle("Updated Ticket Title");
        updatedTicketDto.setDescription("This is the updated description");

        PisteAudit pisteAudit = new PisteAudit();
        pisteAudit.setId("15dd7eac-e0fb-4f63-8316-a8bec10be73f");
        pisteAudit.setAction(Action.UPDATE);
        pisteAudit.setUserConcerned(user.getUsername());
        pisteAudit.setDeleted(false);
        pisteAudit.setLastUpdate(LocalDateTime.now());
        pisteAudit.setTicket(updatedTicket);
        pisteAudit.setUser(null);

        when(userService.getUserByUserName(user.getUsername())).thenReturn(user);
        when(ticketService.getTicketByUserAndId(user, ticketId)).thenReturn(Optional.of(existingTicket));
        when(ticketService.mapToEntity(any(TicketDto.class))).thenReturn(updatedTicket);
        when(ticketService.saveTicket(eq(updatedTicket))).thenReturn(updatedTicket);
        when(ticketService.mapToDto(eq(updatedTicket))).thenReturn(updatedTicketDto);
        doNothing().when(pisteAuditService).savePisteAudit(any(PisteAudit.class));

        mockMvc.perform(put("/tickets/{id}", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketId").value(ticketId))
                .andExpect(jsonPath("$.title").value("Updated Ticket Title"))
                .andExpect(content().json(
                        objectMapper.writeValueAsString(updatedTicketDto)
                ));

        verify(userService).getUserByUserName(user.getUsername());
        verify(ticketService).getTicketByUserAndId(user, ticketId);
        verify(ticketService).mapToEntity(any(TicketDto.class));
        verify(ticketService).saveTicket(eq(updatedTicket));
        verify(ticketService).mapToDto(eq(updatedTicket));
        verify(pisteAuditService).savePisteAudit(any(PisteAudit.class));

    }

    @Test
    @DisplayName("Tester la fonction qui met à jour un ticket existant si le ticket n'existe pas")
    @WithMockUser(username = "test",password = "1234Test$", roles = "")
    public void testUpdateTicket_TicketNotExists() throws Exception {

        // Récupérer l'utilisateur connecté depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = new User();
        user.setUserName(username);

        String ticketId = "4a32d97e-9eba-760d-8cb0-2b789f648b18";

        TicketDto ticketDto = new TicketDto();
        ticketDto.setTitle("Updated Ticket Title");
        ticketDto.setDescription("This is the updated description");

        when(userService.getUserByUserName(user.getUsername())).thenReturn(user);
        when(ticketService.getTicketByUserAndId(user, ticketId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/tickets/{id}", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketDto)))
                .andExpect(status().isBadRequest());

        verify(userService).getUserByUserName(user.getUsername());
        verify(ticketService).getTicketByUserAndId(user, ticketId);

    }

    @ParameterizedTest
    @MethodSource("getVariables")
    @DisplayName("Tester la fonction qui assigne un ticket à un utilisateur si l'utilisateur et le ticket existent")
    @WithMockUser(username = "test",password = "1234Test$", roles = "")
    public void testAssignTicketToUser_TicketAndUserExist(List<Ticket> tickets, List<TicketDto> ticketDtos) throws Exception {

        // Récupérer l'utilisateur connecté depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = new User();
        user.setUserName(username);

        String ticketId = "9a32d97e-9eba-460d-8cb0-9b789f648b18";
        String userId = "be13d331-976d-47ff-bc59-4f0e495e1928";

        User user1 = new User();
        user1.setId(userId);

        Ticket ticket = tickets.get(0);

        Ticket ticketAssign = tickets.get(0);
        ticketAssign.setUser(user1);

        TicketDto ticketDto = ticketDtos.get(0);
        ticketDto.setUserId(userId);

        PisteAudit pisteAudit = new PisteAudit();
        pisteAudit.setId("15dd7eac-e0fb-4f63-8316-a8bec10be73f");
        pisteAudit.setAction(Action.ASSIGN);
        pisteAudit.setUserConcerned(user.getUsername());
        pisteAudit.setDeleted(false);
        pisteAudit.setLastUpdate(LocalDateTime.now());
        pisteAudit.setTicket(ticket);
        pisteAudit.setUser(null);

        when(userService.getUserByUserName(user.getUsername())).thenReturn(user);
        when(ticketService.getTicketByUserAndId(user, ticketId)).thenReturn(Optional.of(ticket));
        when(userService.getUserById(userId)).thenReturn(Optional.of(user1));
        when(ticketService.saveTicket(any(Ticket.class))).thenReturn(ticketAssign);
        when(ticketService.mapToDto(ticketAssign)).thenReturn(ticketDto);
        doNothing().when(pisteAuditService).savePisteAudit(any(PisteAudit.class));

        mockMvc.perform(put("/tickets/{id}/assign/{userId}",ticketId ,userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketId").value(ticketId))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(content().json(
                        objectMapper.writeValueAsString(ticketDto)
                ));

        verify(userService).getUserByUserName(user.getUsername());
        verify(ticketService).getTicketByUserAndId(user, ticketId);
        verify(userService).getUserById(userId);
        verify(ticketService).saveTicket(any(Ticket.class));
        verify(pisteAuditService).savePisteAudit(any(PisteAudit.class));
    }

    @Test
    @DisplayName("Tester la fonction qui assigne un ticket à un utilisateur si le ticket n'existe pas")
    @WithMockUser(username = "test",password = "1234Test$", roles = "")
    public void testAssignTicketToUser_TicketNotExist() throws Exception {

        // Récupérer l'utilisateur connecté depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = new User();
        user.setUserName(username);

        String ticketId = "6a32d97e-9eba-460d-7cb0-8b789f648b18";
        String userId = "be13d331-976d-47ff-bc59-4f0e495e1928";

        when(userService.getUserByUserName(user.getUsername())).thenReturn(user);
        when(ticketService.getTicketByUserAndId(user, ticketId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/tickets/{id}/assign/{userId}", ticketId, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService).getUserByUserName(user.getUsername());
        verify(ticketService).getTicketByUserAndId(user, ticketId);

    }

    @Test
    @DisplayName("Tester la fonction qui assigne un ticket à un utilisateur si l'utilisateur n'existe pas")
    @WithMockUser(username = "test",password = "1234Test$", roles = "")
    public void testAssignTicketToUser_UserNotExist() throws Exception {

        // Récupérer l'utilisateur connecté depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = new User();
        user.setUserName(username);

        String ticketId = "9a32d97e-9eba-460d-8cb0-9b789f648b18";
        String userId = "ce13d331-476d-47ff-bc59-4f0e895e1928";

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setUser(user);

        when(userService.getUserByUserName(user.getUsername())).thenReturn(user);
        when(ticketService.getTicketByUserAndId(user, ticketId)).thenReturn(Optional.of(ticket));
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/tickets/{id}/assign/{userId}", ticketId, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService).getUserByUserName(user.getUsername());
        verify(ticketService).getTicketByUserAndId(user, ticketId);
        verify(userService).getUserById(userId);

    }

    @Test
    @DisplayName("Tester la fonction qui supprime un ticket par son ID si le ticket existe")
    @WithMockUser(username = "test",password = "1234Test$", roles = "")
    public void testDeleteTicket_TicketExists() throws Exception {

        // Récupérer l'utilisateur connecté depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = new User();
        user.setUserName(username);

        String ticketId = "9a32d97e-9eba-460d-8cb0-9b789f648b18";

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setUser(user);

        PisteAudit pisteAudit = new PisteAudit();
        pisteAudit.setId("15dd7eac-e0fb-4f63-8316-a8bec10be73f");
        pisteAudit.setAction(Action.DELETE);
        pisteAudit.setUserConcerned(user.getUsername());
        pisteAudit.setDeleted(false);
        pisteAudit.setLastUpdate(LocalDateTime.now());
        pisteAudit.setTicket(getListVariableTicket().get(0));
        pisteAudit.setUser(null);

        when(userService.getUserByUserName(user.getUsername())).thenReturn(user);
        when(ticketService.getTicketByUserAndId(user, ticketId)).thenReturn(Optional.of(ticket));
        doNothing().when(ticketService).deleteTicket(ticket);
        doNothing().when(pisteAuditService).savePisteAudit(any(PisteAudit.class));

        mockMvc.perform(delete("/tickets/{id}", ticketId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("le ticket avec id: 9a32d97e-9eba-460d-8cb0-9b789f648b18 est supprimé"));

        verify(userService).getUserByUserName(user.getUsername());
        verify(ticketService).getTicketByUserAndId(user, ticketId);
        verify(ticketService).deleteTicket(ticket);
        verify(pisteAuditService).savePisteAudit(any(PisteAudit.class));

    }

    @Test
    @DisplayName("Tester la fonction qui supprime un ticket par son ID si le ticket n'existe pas")
    @WithMockUser(username = "test",password = "1234Test$", roles = "")
    public void testDeleteTicket_TicketNotExists() throws Exception {

        // Récupérer l'utilisateur connecté depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = new User();
        user.setUserName(username);

        String ticketId = "1a32d97e-3eba-450d-8cb0-9b789f648b18";

        when(userService.getUserByUserName(user.getUsername())).thenReturn(user);
        when(ticketService.getTicketByUserAndId(user, ticketId)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/tickets/{id}", ticketId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService).getUserByUserName(user.getUsername());
        verify(ticketService).getTicketByUserAndId(user, ticketId);
    }

    public User getVariableUser(){
        // on utilise le même utilisateur avec WithMockUser pour avoir l'utilisateur connécté
        User user = new User();
        user.setId("be13d331-976d-47ff-bc59-4f0e495e1928");
        user.setUserName("test");
        user.setEmail("test@gmail.com");
        user.setPassword("1234Test$");
        return user;
    }

    public List<Ticket> getListVariableTicket(){
        Ticket ticket1 = new Ticket();
        ticket1.setId("9a32d97e-9eba-460d-8cb0-9b789f648b18");
        ticket1.setUser(getVariableUser());
        ticket1.setTitle("test1 get ticket");
        ticket1.setDescription("test1 pour obtenir un ticket appartient à un utilisateur");

        Ticket ticket2 = new Ticket();
        ticket1.setId("13dd7eac-e0fb-4f63-8319-a8bec10be73f");
        ticket2.setUser(getVariableUser());
        ticket2.setTitle("test2 get ticket");
        ticket2.setDescription("test2 pour obtenir un ticket appartient à un utilisateur");

        return List.of(ticket1,ticket2);
    }

    public List<TicketDto> getListVariableTicketDto(){
        TicketDto ticketDto1 = new TicketDto();
        ticketDto1.setTicketId("9a32d97e-9eba-460d-8cb0-9b789f648b18");
        ticketDto1.setUserId(getVariableUser().getId());
        ticketDto1.setTitle("test1 get ticket");
        ticketDto1.setDescription("test1 pour obtenir un ticket appartient à un utilisateur");

        TicketDto ticketDto2 = new TicketDto();
        ticketDto2.setTicketId("13dd7eac-e0fb-4f63-8319-a8bec10be73f");
        ticketDto1.setUserId(getVariableUser().getId());
        ticketDto1.setTitle("test2 get ticket");
        ticketDto1.setDescription("test2 pour obtenir un ticket appartient à un utilisateur");

        return List.of(ticketDto1, ticketDto2);
    }

    public Stream<Arguments> getVariables(){
        return Stream.of(
                Arguments.of(getListVariableTicket(),getListVariableTicketDto())
        );
    }

}
