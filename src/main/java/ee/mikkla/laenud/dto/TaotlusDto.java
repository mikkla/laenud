package ee.mikkla.laenud.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class TaotlusDto {

	private Long id;

	@NotNull
	private Long summa;

	@NotNull
	private Date tahtaeg;

}
