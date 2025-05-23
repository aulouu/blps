package itmo.blps.service;

import itmo.blps.dto.request.AddressRequest;
import itmo.blps.dto.response.AddressResponse;
import itmo.blps.exceptions.AddressAlreadyExistsException;
import itmo.blps.exceptions.NotValidInputException;
import itmo.blps.exceptions.UserNotFoundException;
import itmo.blps.model.Address;
import itmo.blps.model.User;
import itmo.blps.repository.AddressRepository;
import itmo.blps.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public static void validateAddressRequest(AddressRequest addressRequest) throws IllegalArgumentException {
        if (addressRequest.getCity() == null || addressRequest.getCity().isEmpty() || !addressRequest.getCity().matches("^[a-zA-Z\\s]+$")) {
            throw new NotValidInputException("City must contain only letters");
        }
        if (addressRequest.getStreet() == null || addressRequest.getStreet().isEmpty() || !addressRequest.getStreet().matches("^[a-zA-Z\\s]+$")) {
            throw new NotValidInputException("City must contain only letters");
        }
        if (addressRequest.getBuilding() == null || addressRequest.getBuilding() <= 0) {
            throw new NotValidInputException("Building must be positive");
        }
        if (addressRequest.getEntrance() == null || addressRequest.getEntrance() <= 0) {
            throw new NotValidInputException("Entrance must be positive");
        }
        if (addressRequest.getFloor() == null || addressRequest.getFloor() <= 0) {
            throw new NotValidInputException("Floor must be positive");
        }
        if (addressRequest.getFlat() == null || addressRequest.getFlat() <= 0) {
            throw new NotValidInputException("Flat must be positive");
        }
    }

    public List<AddressResponse> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return modelMapper.map(addresses, new TypeToken<List<AddressResponse>>() {
        }.getType());
    }

    public List<AddressResponse> getUserAddresses(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Username %s not found", username)
                ));
        List<Address> addresses = addressRepository.findByUsersId(user.getId());
        return modelMapper.map(addresses, new TypeToken<List<AddressResponse>>() {
        }.getType());
    }

    public AddressResponse createAddress(AddressRequest addressRequest) {
        validateAddressRequest(addressRequest);
        if (addressRepository.existsByCityAndStreetAndBuildingAndEntranceAndFloorAndFlat(
                addressRequest.getCity(),
                addressRequest.getStreet(),
                addressRequest.getBuilding(),
                addressRequest.getEntrance(),
                addressRequest.getFloor(),
                addressRequest.getFlat())) {
            throw new AddressAlreadyExistsException("Address already exists");
        }
        Address addresses = modelMapper.map(addressRequest, Address.class);
        Address savedAddress = addressRepository.save(addresses);
        return modelMapper.map(savedAddress, AddressResponse.class);
    }
}
