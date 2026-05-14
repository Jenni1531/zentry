package zentry.back.api.bussiness;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.context.annotation.Import;
import zentry.back.api.config.securityConfig;
import zentry.back.api.business.controllers.CartItemsController;
import zentry.back.api.business.dtos.CartItemsRequest;
import zentry.back.api.business.dtos.CartItemsResponse;
import zentry.back.api.business.services.CartItemsService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(securityConfig.class)
@WebMvcTest(CartItemsController.class)
@DisplayName("CartItemsController")
class CartItemsControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @MockBean  CartItemsService service;

    private static final String BASE = "/api/business/cart-items";

    private CartItemsResponse sample() {
        return CartItemsResponse.builder().cartId(1).productId(2).cantidad(3).build();
    }

    @Nested @DisplayName("GET /")
    class ListTests {
        @Test @DisplayName("200 returns page")
        void list_200() throws Exception {
            when(service.list(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(sample())));
            mvc.perform(get(BASE)).andExpect(status().isOk())
               .andExpect(jsonPath("$.content[0].cartId").value(1));
        }
    }

    @Nested @DisplayName("GET /{cartId}/{productId}")
    class GetByIdTests {
        @Test @DisplayName("200 when found")
        void getById_200() throws Exception {
            when(service.getById(1, 2)).thenReturn(sample());
            mvc.perform(get(BASE + "/1/2")).andExpect(status().isOk())
               .andExpect(jsonPath("$.cantidad").value(3));
        }

        @Test @DisplayName("404 when not found")
        void getById_404() throws Exception {
            when(service.getById(9, 9)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
            mvc.perform(get(BASE + "/9/9")).andExpect(status().isNotFound());
        }
    }

    @Nested @DisplayName("POST /")
    class CreateTests {
        @Test @DisplayName("201 on success")
        void create_201() throws Exception {
            CartItemsRequest req = CartItemsRequest.builder().cartId(1).productId(2).cantidad(3).build();
            when(service.create(any())).thenReturn(sample());
            mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(req)))
               .andExpect(status().isCreated()).andExpect(jsonPath("$.cartId").value(1));
        }

        @Test @DisplayName("400 when duplicate item")
        void create_400() throws Exception {
            CartItemsRequest req = CartItemsRequest.builder().cartId(1).productId(2).cantidad(1).build();
            when(service.create(any())).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));
            mvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(req)))
               .andExpect(status().isBadRequest());
        }
    }

    @Nested @DisplayName("PUT /{cartId}/{productId}")
    class UpdateTests {
        @Test @DisplayName("200 on success")
        void update_200() throws Exception {
            CartItemsRequest req = CartItemsRequest.builder().cartId(1).productId(2).cantidad(10).build();
            when(service.update(eq(1), eq(2), any()))
                .thenReturn(CartItemsResponse.builder().cartId(1).productId(2).cantidad(10).build());
            mvc.perform(put(BASE + "/1/2").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(req)))
               .andExpect(status().isOk()).andExpect(jsonPath("$.cantidad").value(10));
        }

        @Test @DisplayName("404 when not found")
        void update_404() throws Exception {
            when(service.update(eq(9), eq(9), any())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
            mvc.perform(put(BASE + "/9/9").contentType(MediaType.APPLICATION_JSON)
               .content(json.writeValueAsString(CartItemsRequest.builder().cartId(9).productId(9).cantidad(1).build())))
               .andExpect(status().isNotFound());
        }
    }

    @Nested @DisplayName("DELETE /{cartId}/{productId}")
    class DeleteTests {
        @Test @DisplayName("204 on success")
        void delete_204() throws Exception {
            doNothing().when(service).delete(1, 2);
            mvc.perform(delete(BASE + "/1/2")).andExpect(status().isNoContent());
        }

        @Test @DisplayName("404 when not found")
        void delete_404() throws Exception {
            doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(service).delete(9, 9);
            mvc.perform(delete(BASE + "/9/9")).andExpect(status().isNotFound());
        }
    }
}
