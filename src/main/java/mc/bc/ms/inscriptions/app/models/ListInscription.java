package mc.bc.ms.inscriptions.app.models;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ListInscription {
	
	@NotBlank
	private String person;
	
}
