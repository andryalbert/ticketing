package com.demo.ticketing.restController;

import com.demo.ticketing.dto.TicketDto;
import com.demo.ticketing.dto.UserDto;
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
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private TicketService ticketService;

    @MockBean
    private PisteAuditService pisteAuditService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @ParameterizedTest
    @MethodSource("getVariables")
    @DisplayName("Tester la fonction qui récupére tous les utilisateurs")
    @WithMockUser(username = "test",password = "1234Test$",roles = "")
    void testGetAllUsers(List<User> users,List<UserDto> userDtos,List<Ticket> tickets,List<TicketDto> ticketDtos) throws Exception {

        User user1 = users.get(0);
        User user2 = users.get(1);

        UserDto userDto1 = userDtos.get(0);
        UserDto userDto2 = userDtos.get(1);

        when(userService.getAllUsers()).thenReturn(users);
        when(userService.mapToDto(user1)).thenReturn(userDto1);
        when(userService.mapToDto(user2)).thenReturn(userDto2);

        mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(userDto1))
                .andExpect(jsonPath("$[1]").value(userDto2))
                .andExpect(content().json(
                        objectMapper.writeValueAsString(userDtos)
                ));

        verify(userService).getAllUsers();
        verify(userService).mapToDto(user1);
        verify(userService).mapToDto(user2);

    }

    @ParameterizedTest
    @MethodSource("getVariables")
    @DisplayName("Tester la fonction qui récupére les tickets assignés à l'utilisateur et l'utilisateur existe")
    @WithMockUser(username = "test",password = "1234Test$",roles = "")
    public void testGetAllTicketsForUser_UserExists(List<User> users,List<UserDto> userDtos,List<Ticket> tickets,List<TicketDto> ticketDtos) throws Exception {
        String userId = "be13d331-976d-47ff-bc59-4f0e495e1928";

        User user1 = getListVariableUser().get(0);

        Ticket ticket1 = tickets.get(0);
        Ticket ticket2 = tickets.get(1);

        TicketDto ticketDto1 = ticketDtos.get(0);
        TicketDto ticketDto2 = ticketDtos.get(1);

        when(userService.getUserById(userId)).thenReturn(Optional.of(user1));
        when(ticketService.getAllTicketsByUser(user1)).thenReturn(tickets);
        when(ticketService.mapToDto(ticket1)).thenReturn(ticketDto1);
        when(ticketService.mapToDto(ticket2)).thenReturn(ticketDto2);

        mockMvc.perform(get("/users/{id}/ticket",userId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(ticketDto1))
                .andExpect(jsonPath("$[1]").value(ticketDto2))
                .andExpect(content().json(
                        objectMapper.writeValueAsString(ticketDtos)
                ));

        verify(userService).getUserById(userId);
        verify(ticketService).getAllTicketsByUser(user1);
        verify(ticketService).mapToDto(ticket1);
        verify(ticketService).mapToDto(ticket2);

    }

    @Test
    @DisplayName("Tester la fonction qui récupére les tickets assignés à l'utilisateur et l'utilisateur n'existe pas")
    @WithMockUser(username = "test",password = "1234Test$",roles = "")
    public void testGetAllTicketsForUser_UserNotExists() throws Exception {
        String userId = "ce13d331-976d-47ff-bc59-4u0e495e1928";

        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/{id}/ticket",userId))
                .andExpect(status().isBadRequest());

        verify(userService).getUserById(userId);
    }

    @ParameterizedTest
    @MethodSource("getVariables")
    @DisplayName("Tester la fonction qui crée un utilisateur")
    @WithMockUser(username = "test",password = "1234Test$",roles = "")
    public void testCreateUser(List<User> users,List<UserDto> userDtos,List<Ticket> tickets,List<TicketDto> ticketDtos) throws Exception {

        User user = users.get(0);
        UserDto userDto = userDtos.get(0);

        PisteAudit pisteAudit = new PisteAudit();
        pisteAudit.setId("15dd7eac-e0fb-4f63-8316-a8bec10be73f");
        pisteAudit.setAction(Action.CREATE);
        pisteAudit.setUserConcerned(user.getUsername());
        pisteAudit.setDeleted(false);
        pisteAudit.setLastUpdate(LocalDateTime.now());
        pisteAudit.setTicket(null);
        pisteAudit.setUser(user);

        when(userService.mapToEntity(userDto)).thenReturn(user);
        when(userService.saveUser(user)).thenReturn(user);
        when(userService.mapToDto(user)).thenReturn(userDto);
        doNothing().when(pisteAuditService).savePisteAudit(any(PisteAudit.class));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(userDto)
                ));

        verify(userService).mapToEntity(userDto);
        verify(userService).saveUser(user);
        verify(userService).mapToDto(user);
        verify(pisteAuditService).savePisteAudit(any(PisteAudit.class));

    }

    @ParameterizedTest
    @MethodSource("getVariables")
    @DisplayName("Tester la fonction qui modifie un utilisateur si l'utilisateur existe")
    @WithMockUser(username = "test",password = "1234Test$",roles = "")
    public void testUpdateUser_UserExists(List<User> users,List<UserDto> userDtos,List<Ticket> tickets,List<TicketDto> ticketDtos) throws Exception {

        String userId = "be13d331-976d-47ff-bc59-4f0e495e1928";

        UserDto userDto = userDtos.get(1);
        userDto.setUserId(userId);

        User updatedUser = users.get(1);
        updatedUser.setId(userId);

        PisteAudit pisteAudit = new PisteAudit();
        pisteAudit.setId("15dd7eac-e0fb-4f63-8316-a8bec10be73f");
        pisteAudit.setAction(Action.UPDATE);
        pisteAudit.setUserConcerned(getListVariableUser().get(0).getUsername());
        pisteAudit.setDeleted(false);
        pisteAudit.setLastUpdate(LocalDateTime.now());
        pisteAudit.setTicket(null);
        pisteAudit.setUser(updatedUser);

        when(userService.getUserById(userId)).thenReturn(Optional.of(users.get(0)));
        when(userService.mapToEntity(userDto)).thenReturn(updatedUser);
        when(userService.saveUser(updatedUser)).thenReturn(updatedUser);
        when(userService.mapToDto(updatedUser)).thenReturn(userDto);
        doNothing().when(pisteAuditService).savePisteAudit(any(PisteAudit.class));

        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtos.get(1))))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        objectMapper.writeValueAsString(userDto)
                ));

        verify(userService).getUserById(userId);
        verify(userService).mapToEntity(userDto);
        verify(userService).saveUser(updatedUser);
        verify(userService).mapToDto(updatedUser);
        verify(pisteAuditService).savePisteAudit(any(PisteAudit.class));

    }

    @ParameterizedTest
    @MethodSource("getVariables")
    @DisplayName("Tester la fonction qui modifie un utilisateur si l'utilisateur n'existe pas")
    @WithMockUser(username = "test",password = "1234Test$",roles = "")
    public void testUpdateUser_UserNotExists(List<User> users,List<UserDto> userDtos,List<Ticket> tickets,List<TicketDto> ticketDtos) throws Exception {

        String userId = "ze13c331-956d-47ff-bc59-4h0e495e1928";

        UserDto userDto = userDtos.get(0);

        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userService).getUserById(userId);

    }

    public List<User> getListVariableUser(){
        User user1 = new User();
        user1.setId("be13d331-976d-47ff-bc59-4f0e495e1928");
        user1.setUserName("test1");
        user1.setEmail("test1@gmail.com");
        user1.setPassword("1234Test1$");

        User user2 = new User();
        user2.setId("dc13d331-971d-45ff-bc52-4f0e455e1926");
        user2.setUserName("test2");
        user2.setEmail("test2@gmail.com");
        user2.setPassword("1234Test1$");

        return List.of(user1,user2);
    }

    public List<UserDto> getListVariableUserDto(){
        UserDto userDto1 = new UserDto();
        userDto1.setUserId("be13d331-976d-47ff-bc59-4f0e495e1928");
        userDto1.setUsername("test1");
        userDto1.setEmail("test1@gmail.com");
        userDto1.setPassword("1234Test1$");

        UserDto userDto2 = new UserDto();
        userDto2.setUserId("dc13d331-971d-45ff-bc52-4f0e455e1926");
        userDto2.setUsername("test2");
        userDto2.setEmail("test2@gmail.com");
        userDto2.setPassword("1234Test1$");

        return List.of(userDto1,userDto2);
    }

    public List<Ticket> getListVariableTicket(){
        Ticket ticket1 = new Ticket();
        ticket1.setId("9a32d97e-9eba-460d-8cb0-9b789f648b18");
        ticket1.setUser(getListVariableUser().get(0));
        ticket1.setTitle("test1 get ticket");
        ticket1.setDescription("test1 pour obtenir un ticket appartient à un utilisateur");

        Ticket ticket2 = new Ticket();
        ticket1.setId("13dd7eac-e0fb-4f63-8319-a8bec10be73f");
        ticket2.setUser(getListVariableUser().get(0));
        ticket2.setTitle("test2 get ticket");
        ticket2.setDescription("test2 pour obtenir un ticket appartient à un utilisateur");

        return List.of(ticket1,ticket2);
    }

    public List<TicketDto> getListVariableTicketDto(){
        TicketDto ticketDto1 = new TicketDto();
        ticketDto1.setUserId(getListVariableUser().get(0).getId());
        ticketDto1.setTitle("test1 get ticket");
        ticketDto1.setDescription("test1 pour obtenir un ticket appartient à un utilisateur");

        TicketDto ticketDto2 = new TicketDto();
        ticketDto1.setUserId(getListVariableUser().get(0).getId());
        ticketDto1.setTitle("test2 get ticket");
        ticketDto1.setDescription("test2 pour obtenir un ticket appartient à un utilisateur");

        return List.of(ticketDto1, ticketDto2);
    }

    public Stream<Arguments> getVariables(){
        return Stream.of(
                Arguments.of(getListVariableUser(),getListVariableUserDto(),getListVariableTicket(),getListVariableTicketDto())
        );
    }


}
