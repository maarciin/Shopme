package com.shopme.setting;

import com.shopme.common.entity.StateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StateRestController {

    private final StateRepository stateRepository;

    @GetMapping("/settings/statesByCountry/{countryId}")
    public List<StateDto> listByCountry(@PathVariable int countryId) {
        return stateRepository.findByCountry_IdOrderByName(countryId)
                .stream()
                .map(state -> new StateDto(state.getId(), state.getName()))
                .toList();
    }

}
