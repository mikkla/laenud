package ee.mikkla.laenud.controller;

import ee.mikkla.laenud.repository.IsikRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ee.mikkla.laenud.dto.IsikDto;
import ee.mikkla.laenud.model.Isik;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/isik")
public class IsikController {

	@Autowired
	private IsikRepository repository;

	@Operation(summary = "Tagastab kõik isikud")
	@GetMapping
	public ResponseEntity<List<IsikDto>> findAllIsik() {
		List<IsikDto> result = repository.findAll()
				.stream()
				.map(this::toDto)
				.collect(Collectors.toList());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@Operation(summary = "Tagastab konkreetse isiku")
	@GetMapping(value = "/{isikId}")
	public ResponseEntity<IsikDto> findIsikById(@PathVariable("isikId") Long isikId) {
		Optional<Isik> isik = repository.findById(isikId);
		if (isik.isPresent()) {
			return new ResponseEntity<>(toDto(isik.get()), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Tekitab uue isiku")
	@PostMapping
	public ResponseEntity<IsikDto> createIsik(@Valid @RequestBody IsikDto dto) {
		Isik entity = new Isik();
		updateEntity(entity, dto);
		return new ResponseEntity<>(toDto(repository.save(entity)), HttpStatus.CREATED);
	}

	@Operation(summary = "Uuendab isiku andmeid")
	@PutMapping("/{isikId}")
	public ResponseEntity<IsikDto> updateIsik(@PathVariable Long isikId, @Valid @RequestBody IsikDto dto) {
		Optional<Isik> isik = repository.findById(isikId);
		if (isik.isPresent()) {
			Isik entity = isik.get();
			updateEntity(entity, dto);
			return new ResponseEntity<>(toDto(repository.save(entity)), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Operation(summary = "Kustutab isiku")
	@DeleteMapping("/{isikId}")
	public ResponseEntity<HttpStatus> deleteIsik(@PathVariable Long isikId) {
		try {
			repository.deleteById(isikId);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private IsikDto toDto(Isik entity) {
		IsikDto dto = new IsikDto();
		dto.setId(entity.getId());
		dto.setAadress(entity.getAadress());
		dto.setEpost(entity.getEpost());
		dto.setNimi(entity.getNimi());
		dto.setSynniaeg(entity.getSynniaeg());
		return dto;
	}

	private void updateEntity(Isik entity, IsikDto dto) {
		entity.setAadress(dto.getAadress());
		entity.setEpost(dto.getEpost());
		entity.setNimi(dto.getNimi());
		entity.setSynniaeg(dto.getSynniaeg());
	}

}
