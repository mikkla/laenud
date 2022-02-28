package ee.mikkla.laenud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ee.mikkla.laenud.model.Taotlus;

import java.util.List;

@Repository
public interface TaotlusRepository extends JpaRepository<Taotlus, Long> {

    List<Taotlus> findAllByIsikId(Long isikId);

}
