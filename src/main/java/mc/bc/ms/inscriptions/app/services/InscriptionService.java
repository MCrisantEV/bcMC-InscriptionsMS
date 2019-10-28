package mc.bc.ms.inscriptions.app.services;

import java.util.Map;

import mc.bc.ms.inscriptions.app.models.Inscription;
import reactor.core.publisher.Mono;

public interface InscriptionService {

	public Mono<Map<String, Object>> saveInscription(Inscription inscription);

}
