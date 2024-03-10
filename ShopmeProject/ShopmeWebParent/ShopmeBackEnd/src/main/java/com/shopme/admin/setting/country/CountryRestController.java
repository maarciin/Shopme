package com.shopme.admin.setting.country;

import com.shopme.common.entity.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CountryRestController {

    private final CountryRepository countryRepository;

    @GetMapping("/countries")
    public List<Country> listAll() {
        return countryRepository.findAllByOrderByName();
    }

    @PostMapping("/countries")
    public String save(@RequestBody Country country) {
        Country savedCountry = countryRepository.save(country);
        return String.valueOf(savedCountry.getId());
    }

    @DeleteMapping("/countries/{id}")
    public void delete(@PathVariable Integer id) {
        countryRepository.deleteById(id);
    }

}
