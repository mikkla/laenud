package ee.mikkla.laenud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ee.mikkla.laenud.model.Isik;

@Repository
public interface IsikRepository extends JpaRepository<Isik, Long> {

}
