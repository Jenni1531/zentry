package zentry.back.api.bussiness;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.context.annotation.Import;
import zentry.back.api.config.securityConfig;
import zentry.back.api.business.controllers.OrderItemsController;
import zentry.back.api.business.dtos.OrderItemsRequest;
import zentry.back.api.business.dtos.OrderItemsResponse;
import zentry.back.api.business.services.OrderItemsService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(securityConfig.class)
@WebMvcTest(OrderItemsController.class)
@DisplayName("OrderItemsController")
class OrderItemsControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @MockBean  OrderItemsService service;

    private static final String BASE = "/api/business/order-items";

    private OrderItemsResponse sample() {
        return OrderItemsResponse.builder().orderId(10).productId(20).cantidad(5).build();
    }

    @Nested @DisplayName("GET /") class ListTests {
        @Test void list_200() throws Exception {
            when(service.list(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(sample())));
            mvc.perform(get(BASE)).andExpect(status().isOk())
               .andExpect(jsonPath("$.content[0].orderId").value(10));
        }
    }

    @Nested @DisplayName("GET /{orderId}/{productId}") class GetByIdTests {
        @Test void getById_200() throws Exception {
            when(service.getById(10, 20)).thenReturn(sample());
            mvc.perform(get(BASE + "/10/20")).andExpect(status().isOk())
               .andExpect(jsonPath("$.cantidad").value(5));
        }
        @Test void getById_404() throws Exception {
            when(service.getById(9, 9)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
            mvc.perform(get(BASE + "/9/9")).andExpect(status().isNotFound());
        }
    }

    @Nested @DisplayName("POST /") class CreateTests {
        @Test void create_201() throws Exception {
            OrderItemsRequest req = OrderItemsRequest.builder().orderId(10).productId(20).cantidad(5).build();
            when(service.create(any())).thenReturn(sample());
            mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(req)))
               .andExpect(status().isCreated()).andExpect(jsonPath("$.orderId").value(10));
        }
        @Test void create_400_duplicate() throws Exception {
            when(service.create(any())).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));
            mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON)
               .content(json.writeValueAsString(OrderItemsRequest.builder().orderId(10).productId(20).cantidad(1).build())))
               .andExpect(status().isBadRequest());
        }
    }

    @Nested @DisplayName("PUT /{orderId}/{productId}") class UpdateTests {
        @Test void update_200() throws Exception {
            OrderItemsRequest req = OrderItemsRequest.builder().orderId(10).productId(20).cantidad(99).build();
            when(service.update(eq(10), eq(20), any()))
                .thenReturn(OrderItemsResponse.builder().orderId(10).productId(20).cantidad(99).build());
            mvc.perform(put(BASE + "/10/20").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(req)))
               .andExpect(status().isOk()).andExpect(jsonPath("$.cantidad").value(99));
        }
        @Test void update_404() throws Exception {
            when(service.update(eq(9), eq(9), any())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
            mvc.perform(put(BASE + "/9/9").contentType(MediaType.APPLICATION_JSON)
               .content(json.writeValueAsString(OrderItemsRequest.builder().orderId(9).productId(9).cantidad(1).build())))
               .andExpect(status().isNotFound());
        }
    }

    @Nested @DisplayName("DELETE /{orderId}/{productId}") class DeleteTests {
        @Test void delete_204() throws Exception {
            doNothing().when(service).delete(10, 20);
            mvc.perform(delete(BASE + "/10/20")).andExpect(status().isNoContent());
        }
        @Test void delete_404() throws Exception {
            doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(service).delete(9, 9);
            mvc.perform(delete(BASE + "/9/9")).andExpect(status().isNotFound());
        }
    }
}
