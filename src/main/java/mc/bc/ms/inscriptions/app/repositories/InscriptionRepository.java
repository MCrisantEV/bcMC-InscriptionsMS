package mc.bc.ms.inscriptions.app.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import mc.bc.ms.inscriptions.app.models.Inscription;
import reactor.core.publisher.Mono;

public interface InscriptionRepository extends ReactiveMongoRepository<Inscription, String>{
	
	public Mono<Inscription> findByCourse(String course);
}
