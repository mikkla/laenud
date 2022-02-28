package ee.mikkla.laenud.dto;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class IsikDto {

	private Long id;

	@NotEmpty
	private String nimi;

	@NotEmpty
	private String epost;

	@NotNull
	private Date synniaeg;

	@NotEmpty
	private String aadress;

}
