package com.shopme.admin.setting.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StateRestControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    StateRepository stateRepository;

    @Autowired
    CountryRepository countryRepository;

    @Test
    //fake user
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", roles = "ADMIN")
    public void testListStatesByCountry() throws Exception {
        int countryId = 3;
        String url = "/states/" + countryId;
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        State[] states = objectMapper.readValue(jsonResponse, State[].class);

        assertThat(states).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", roles = "ADMIN")
    public void testCreateState() throws Exception {
        String url = "/states";
        Integer countryId = 1;
        Country country = countryRepository.findById(countryId).get();
        State state = new State("Śląsk", country);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(state))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        int stateId = Integer.parseInt(response);

        Optional<State> findById = stateRepository.findById(stateId);
        assertThat(findById).isPresent();

        State savedState = findById.get();
        assertThat(savedState.getName()).isEqualTo("Śląsk");

    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", roles = "ADMIN")
    public void testUpdateState() throws Exception {
        String url = "/states";
        Integer stateId = 10;
        String newStateName = "Arizona";

        State state = stateRepository.findById(stateId).get();
        state.setName(newStateName);

        mockMvc.perform(MockMvcRequestBuilders.post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(state))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(stateId)));

        Optional<State> findById = stateRepository.findById(stateId);
        assertThat(findById).isPresent();

        State savedState = findById.get();
        assertThat(savedState.getName()).isEqualTo(newStateName);

    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", roles = "ADMIN")
    public void testDeleteState() throws Exception {
        Integer stateId = 11;
        String url = "/states/" + stateId;

        mockMvc.perform(MockMvcRequestBuilders.delete(url).with(csrf()))
                .andExpect(status().isOk());

        Optional<State> findById = stateRepository.findById(stateId);
        assertThat(findById).isEmpty();
    }
}