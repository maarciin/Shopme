package com.shopme.admin.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderRestControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user1", password = "pass1", authorities = {"Shipper"})
    public void shouldReturnUpdatedStatusWhenValidOrderIdAndStatusProvided() throws Exception {
        Integer orderId = 11;
        String status = "SHIPPING";
        String requestURL = "/orders_shipper/update/" + orderId + "/" + status;

        mockMvc.perform(post(requestURL)
                        .contentType("application/json")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

    }

}