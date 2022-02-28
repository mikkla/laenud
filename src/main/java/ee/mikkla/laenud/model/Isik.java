package ee.mikkla.laenud.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Isik {
	
	@Id
	@SequenceGenerator(name = "seq_isik", sequenceName = "seq_isik", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_isik")
	private Long id;

	private String nimi;

	private String epost;

	private Date synniaeg;

	private String aadress;

}
