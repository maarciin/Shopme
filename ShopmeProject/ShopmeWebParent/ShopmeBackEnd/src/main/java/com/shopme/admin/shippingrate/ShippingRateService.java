package com.shopme.admin.shippingrate;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing shipping rates.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ShippingRateService {

    public static final int RATES_PER_PAGE = 10;
    private final ShippingRateRepository shippingRateRepository;
    private final CountryRepository countryRepository;

    /**
     * Lists entities by page using a helper object for paging and sorting.
     *
     * @param pageNum The page number to retrieve.
     * @param helper  The helper object that handles paging and sorting.
     */
    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, RATES_PER_PAGE, shippingRateRepository);
    }

    /**
     * Saves a shipping rate, throwing an exception if a rate already exists for the same destination.
     *
     * @param rateToSave The shipping rate to save.
     * @throws ShippingRateAlreadyExistsException if a rate already exists for the same destination.
     */
    public void save(ShippingRate rateToSave) throws ShippingRateAlreadyExistsException {
        Optional<ShippingRate> shippingRate = shippingRateRepository.findByCountryAndState(
                rateToSave.getCountry().getId(), rateToSave.getState());
        boolean foundExistingRateInNewMode = rateToSave.getId() == null && shippingRate.isPresent();
        boolean foundDifferentExistingRateInEditMode =
                rateToSave.getId() != null && shippingRate.isPresent() && !shippingRate.get().equals(rateToSave);
        if (foundExistingRateInNewMode || foundDifferentExistingRateInEditMode) {
            throw new ShippingRateAlreadyExistsException("There's already a rate for the destination "
                    + rateToSave.getCountry().getName() + ", " + rateToSave.getState());
        }
        shippingRateRepository.save(rateToSave);
    }

    /**
     * Updates the COD support status of a shipping rate.
     *
     * @param id           The ID of the shipping rate to update.
     * @param codSupported The new COD support status.
     * @throws ShippingRateNotFoundException if no shipping rate with the given ID is found.
     */
    public void updateCODSupportStatus(Integer id, boolean codSupported) throws ShippingRateNotFoundException {
        if (!shippingRateRepository.existsById(id)) {
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
        }
        shippingRateRepository.updateCODSupport(id, codSupported);
    }

    /**
     * Deletes a shipping rate.
     *
     * @param id The ID of the shipping rate to delete.
     * @throws ShippingRateNotFoundException if no shipping rate with the given ID is found.
     */
    public void deleteShippingRate(Integer id) throws ShippingRateNotFoundException {
        if (!shippingRateRepository.existsById(id)) {
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
        }
        shippingRateRepository.deleteById(id);
    }

    /**
     * Lists all countries.
     *
     * @return A list of all countries.
     */
    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByName();
    }

    /**
     * Retrieves a shipping rate by its ID.
     *
     * @param id The ID of the shipping rate to retrieve.
     * @return The shipping rate with the given ID.
     * @throws ShippingRateNotFoundException if no shipping rate with the given ID is found.
     */
    public ShippingRate get(Integer id) throws ShippingRateNotFoundException {
        return shippingRateRepository.findById(id)
                .orElseThrow(() -> new ShippingRateNotFoundException("Could not find shipping rate with ID " + id));
    }
}