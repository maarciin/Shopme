package com.shopme.admin.setting.state;

import com.shopme.common.entity.State;
import com.shopme.common.entity.StateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StateRestController {

    private final StateRepository stateRepository;

    @GetMapping("/states/{countryId}")
    public List<StateDto> listByCountry(@PathVariable int countryId) {
        return stateRepository.findByCountry_IdOrderByName(countryId)
                .stream()
                .map(state -> new StateDto(state.getId(), state.getName()))
                .toList();
    }

    @PostMapping("/states")
    public String save(@RequestBody State state) {
        State savedState = stateRepository.save(state);
        return String.valueOf(savedState.getId());
    }

    @DeleteMapping("/states/{id}")
    public void delete(@PathVariable Integer id) {
        stateRepository.deleteById(id);
    }

}
