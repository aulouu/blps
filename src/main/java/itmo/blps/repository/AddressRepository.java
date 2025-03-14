package itmo.blps.repository;

import itmo.blps.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>  {
    boolean existsByCityAndStreetAndBuildingAndEntranceAndFloorAndFlat(String city, String street, Integer building, Integer entrance, Integer floor, Integer flat);
    Optional<Address> findByCityAndStreetAndBuildingAndEntranceAndFloorAndFlat(String city, String street, Integer building, Integer entrance, Integer floor, Integer flat);
}
