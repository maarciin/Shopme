package com.shopme.admin.setting.country;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.common.entity.Country;
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
public class CountryRestControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CountryRepository countryRepository;

    @Test
    //fake user
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", roles = "ADMIN")
    public void testListCountries() throws Exception {
        String url = "/countries";
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Country[] countries = objectMapper.readValue(jsonResponse, Country[].class);

        assertThat(countries).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", roles = "ADMIN")
    public void testCreateCountry() throws Exception {
        String url = "/countries";
        String countryName = "France";
        String countryCode = "FR";
        Country country = new Country(countryName, countryCode);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(country))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        int countryId = Integer.parseInt(response);

        Optional<Country> findById = countryRepository.findById(countryId);
        assertThat(findById).isPresent();

        Country savedCountry = findById.get();
        assertThat(savedCountry.getName()).isEqualTo(countryName);

    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", roles = "ADMIN")
    public void testUpdateCountry() throws Exception {
        String url = "/countries";
        Integer countryId = 6;
        String countryName = "China";
        String countryCode = "CN";
        Country country = new Country(countryId, countryName, countryCode);

        mockMvc.perform(MockMvcRequestBuilders.post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(country))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(countryId)));

        Optional<Country> findById = countryRepository.findById(countryId);
        assertThat(findById).isPresent();

        Country savedCountry = findById.get();
        assertThat(savedCountry.getName()).isEqualTo(countryName);

    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "nam2020", roles = "ADMIN")
    public void testDeleteCountry() throws Exception {
        Integer countryId = 8;
        String url = "/countries/" + countryId;

        mockMvc.perform(MockMvcRequestBuilders.delete(url).with(csrf()))
                .andExpect(status().isOk());

        Optional<Country> findById = countryRepository.findById(countryId);
        assertThat(findById).isEmpty();
    }

}