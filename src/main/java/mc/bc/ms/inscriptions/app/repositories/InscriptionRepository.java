package mc.bc.ms.inscriptions.app.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import mc.bc.ms.inscriptions.app.models.Inscription;
import reactor.core.publisher.Flux;

public interface InscriptionRepository extends ReactiveMongoRepository<Inscription, String>{
	
	public Flux<Inscription> findByInstituteAndStudentsPersonIn(String institute, String person);
	
	public Flux<Inscription> findByInstituteAndFamilyMembersPersonIn(String institute, String person);
	
	
}
