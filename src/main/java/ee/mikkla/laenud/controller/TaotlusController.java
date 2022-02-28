package ee.mikkla.laenud.controller;

import ee.mikkla.laenud.dto.IsikDto;
import ee.mikkla.laenud.dto.TaotlusDto;
import ee.mikkla.laenud.model.Isik;
import ee.mikkla.laenud.model.Taotlus;
import ee.mikkla.laenud.repository.IsikRepository;
import ee.mikkla.laenud.repository.TaotlusRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/taotlus")
public class TaotlusController {

    @Autowired
    private TaotlusRepository repository;

    @Autowired
    private IsikRepository isikRepository;

    @Operation(summary = "Tagastab kõik taotlused")
    @GetMapping
    public ResponseEntity<List<TaotlusDto>> findAllTaotlus() {
        List<TaotlusDto> result = repository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Tagastab konkreetse isiku kõik taotlused")
    @GetMapping(value = "/{isikId}")
    public ResponseEntity<List<TaotlusDto>> findAllTaotlusByIsikId(@PathVariable("isikId") Long isikId) {
        List<TaotlusDto> result = repository.findAllByIsikId(isikId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Tekitab konkreetsele isikule uue taotluse")
    @PostMapping(value = "/{isikId}")
    public ResponseEntity<TaotlusDto> createTaotlusForIsik(@PathVariable("isikId") Long isikId, @Valid @RequestBody TaotlusDto dto) {
        Optional<Isik> isik = isikRepository.findById(isikId);
        if (isik.isPresent()) {
            Isik isikEntity = isik.get();
            Date synniaeg = isikEntity.getSynniaeg();
            LocalDate born = synniaeg.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate today = LocalDate.now();
            if (today.minusYears(18).isBefore(born)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Taotlus entity = new Taotlus();
            updateEntity(entity, dto);
            entity.setIsik(isikEntity);
            return new ResponseEntity<>(toDto(repository.save(entity)), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Uuendab taotluse andmeid")
    @PutMapping("/{taotlusId}")
    public ResponseEntity<TaotlusDto> updateTaotlus(@PathVariable Long taotlusId, @Valid @RequestBody TaotlusDto dto) {
        Optional<Taotlus> taotlus = repository.findById(taotlusId);
        if (taotlus.isPresent()) {
            Taotlus entity = taotlus.get();
            updateEntity(entity, dto);
            return new ResponseEntity<>(toDto(repository.save(entity)), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Kustutab taotluse")
    @DeleteMapping("/{taotlusId}")
    public ResponseEntity<HttpStatus> deleteTaotlus(@PathVariable Long taotlusId) {
        try {
            repository.deleteById(taotlusId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private TaotlusDto toDto(Taotlus entity) {
        TaotlusDto dto = new TaotlusDto();
        dto.setId(entity.getId());
        dto.setSumma(entity.getSumma());
        dto.setTahtaeg(entity.getTahtaeg());
        return dto;
    }

    private void updateEntity(Taotlus entity, TaotlusDto dto) {
        entity.setSumma(dto.getSumma());
        entity.setTahtaeg(dto.getTahtaeg());
    }

}
