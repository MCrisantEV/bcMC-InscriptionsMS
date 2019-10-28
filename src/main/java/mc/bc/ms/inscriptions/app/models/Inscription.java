package mc.bc.ms.inscriptions.app.models;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "inscriptions")
public class Inscription {
	@Id
	private String id;

	@NotBlank
	private String course;

	private List<ListInscription> students;

	private List<ListInscription> familyMembers;

}
