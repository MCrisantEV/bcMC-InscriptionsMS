package mc.bc.ms.inscriptions.app.models;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "inscriptions")
public class Inscription {
	@NotBlank
	private String id;

	private List<ListInscription> students;

	private List<ListInscription> familyMembers;

}
