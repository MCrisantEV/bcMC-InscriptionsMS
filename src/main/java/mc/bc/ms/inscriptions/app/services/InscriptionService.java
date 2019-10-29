package mc.bc.ms.inscriptions.app.services;

import java.util.Map;

import mc.bc.ms.inscriptions.app.models.Inscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InscriptionService {

	public Mono<Map<String, Object>> saveInscription(Inscription inscription);
	
	public Flux<Inscription> findAllInscription();
	
	public Mono<Inscription> findIdInscription(String id);
	
	public Mono<Map<String, Object>> updateInscription(String id, Inscription inscription);
	
	public Mono<Map<String, Object>> deleteInscription(String id);

}
