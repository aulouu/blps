package itmo.blps.repository;

import itmo.blps.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>  {
    boolean existsByCityAndStreetAndBuildingAndEntranceAndFloorAndFlat(String city, String street, String building, String entrance, String floor, String flat);
}
