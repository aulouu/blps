package itmo.blps.service;

import itmo.blps.dto.request.AddressRequest;
import itmo.blps.dto.response.AddressResponse;
import itmo.blps.model.Address;
import itmo.blps.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    public List<AddressResponse> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return modelMapper.map(addresses, new TypeToken<List<AddressResponse>>(){}.getType());
    }

    public AddressResponse getAddressById(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new IllegalArgumentException("Address not found");
        }
        return modelMapper.map(addressRepository.findById(id), AddressResponse.class);
    }

    public AddressResponse createAddress(AddressRequest addressRequest) {
        if (addressRepository.existsByCityAndStreetAndBuildingAndEntranceAndFloorAndFlat(
                addressRequest.getCity(),
                addressRequest.getStreet(),
                addressRequest.getBuilding(),
                addressRequest.getEntrance(),
                addressRequest.getFloor(),
                addressRequest.getFlat())) {
            throw new IllegalArgumentException("Address already exists");
        };
        Address addresses = modelMapper.map(addressRequest, Address.class);
        Address savedAddress = addressRepository.save(addresses);
        return modelMapper.map(savedAddress, AddressResponse.class);
    }

}
