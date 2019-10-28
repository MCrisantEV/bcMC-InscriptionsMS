package mc.bc.ms.inscriptions.app.impl;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.client.WebClient;

import mc.bc.ms.inscriptions.app.models.Course;
import mc.bc.ms.inscriptions.app.models.Inscription;
import mc.bc.ms.inscriptions.app.repositories.InscriptionRepository;
import mc.bc.ms.inscriptions.app.services.InscriptionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class InscriptionImpl implements InscriptionService {
	
	@Autowired
	private InscriptionRepository insRep;
	
	@Autowired
	private Validator validator;
	
	@Autowired
	private WebClient client;

	@Override
	public Mono<Map<String, Object>> saveInscription(Inscription inscription) {
		Map<String, Object> respuesta = new HashMap<String, Object>();
		return Mono.just(inscription).flatMap(inscp -> {

			Errors errors = new BeanPropertyBindingResult(inscp, Inscription.class.getName());
			validator.validate(inscp, errors);
			
//			validate empty data
			if (errors.hasErrors()) {
				return Flux.fromIterable(errors.getFieldErrors()).map(err -> {
					String[] matriz = { err.getField(), err.getDefaultMessage() };
					return matriz;
				}).collectList().flatMap(l -> {
					respuesta.put("status", HttpStatus.BAD_REQUEST.value());
					respuesta.put("Mensaje", "Error, revise los datos");
					l.forEach(m -> {
						for (int i = 0; i < m.length; i++) { respuesta.put(m[0], m[i]);}
					});
					return Mono.just(respuesta);
				});
			} else {
//				validate course data
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("id", inscp.getCourse());
				
				return insRep.findByCourse(inscp.getCourse()).map(db -> {
					respuesta.put("Error", "El curso ya tiene una inscrpción");
					return respuesta;
				})
				.switchIfEmpty(
					client.get().uri("/{id}", params)
					.accept(APPLICATION_JSON_UTF8)
					.retrieve().bodyToMono(Course.class)
					.map(c -> {
						if(c.getState().equals("Open")) {
							insRep.save(inscp).subscribe();
							respuesta.put("Mensaje", "Curso "+ c.getName() +", abrió el proceso de inscripciones con éxito");
						}else {
							respuesta.put("Error", "No se puede abrir las inscripciones");
							respuesta.put("Mensaje", "Curso "+c.getName()+" tiene el state '"+c.getState()+"'");
						}
						return respuesta;
					}).switchIfEmpty(
						Mono.just(inscp).map(er -> {
							respuesta.put("Error", "El curso no existe");
							return respuesta;
						})
					));
			}
		});
	}
}
