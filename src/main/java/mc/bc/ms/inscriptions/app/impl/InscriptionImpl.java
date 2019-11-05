package mc.bc.ms.inscriptions.app.impl;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import mc.bc.ms.inscriptions.app.models.ListInscription;
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
				params.put("id", inscp.getId());
				
				return insRep.findById(inscp.getId()).map(db -> {
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

	@Override
	public Flux<Inscription> findAllInscription() {
		return insRep.findAll();
	}

	@Override
	public Mono<Inscription> findIdInscription(String id) {
		return insRep.findById(id);
	}

	@Override
	public Mono<Map<String, Object>> updateInscription(String id, Inscription inscription) {
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
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("id", id);
				
				return insRep.findById(id).flatMap(db -> {
					return client.get().uri("/{id}", params)
							.accept(APPLICATION_JSON_UTF8)
							.retrieve().bodyToMono(Course.class).map(c -> {
								if(inscp.getStudents().size() < c.getMin()) {
									inscp.setFamilyMembers(db.getFamilyMembers());
									insRep.save(inscp).subscribe();
									respuesta.put("Mensaje", "Se agrego con éxito "+inscp.getStudents().size()+" estudiantes.");
									respuesta.put("Warning", "Solo se pueden registrar estudiantes porque no cumplen el mínimo del cupo.");
								}else {
									int total = inscp.getStudents().size() + inscp.getFamilyMembers().size();
									if(total <= c.getMax()) {
										insRep.save(inscp).subscribe();
										respuesta.put("Success", "Se agrego con éxito.");
										respuesta.put("Mensaje", inscp.getStudents().size()+" Estudiantes - "+inscp.getFamilyMembers().size()+" Familiares.");
									}else {
										if(inscp.getStudents().size() <= c.getMax()) {
											Mono.just(inscp).doOnNext(m -> {
												List<ListInscription> list = new ArrayList<>();
												int dif = c.getMax() - inscp.getStudents().size();
												for (int i = 0; i < dif; i++) {
													ListInscription objins = new ListInscription();
													objins.setPerson(inscp.getFamilyMembers().get(i).getPerson());
													list.add(objins);
												}
												inscp.setFamilyMembers(list);
											}).subscribe();
											insRep.save(inscp).subscribe();
										}else {
											Mono.just(inscp).doOnNext(m -> {
												List<ListInscription> list = new ArrayList<>();
												int dif = c.getMax() - inscp.getStudents().size();
												for (int i = 0; i < dif; i++) {
													ListInscription objins = new ListInscription();
													objins.setPerson(inscp.getStudents().get(i).getPerson());
													list.add(objins);
												}
												inscp.setFamilyMembers(list);
											}).subscribe();
											inscp.setFamilyMembers(null);
											insRep.save(inscp).subscribe();
										}
										respuesta.put("Success", "Se agrego con éxito.");
										respuesta.put("Mensaje", inscp.getStudents().size()+" Estudiantes - "+inscp.getFamilyMembers().size()+" Familiares.");
										respuesta.put("Warning", "Por falta de cupo no se registraron todos los familiares.");
									}
								}
								c.setState("Active");
								client.put().uri("/{id}", params)
								.contentType(APPLICATION_JSON_UTF8)
								.syncBody(c)
						     	.retrieve()
						     	.bodyToMono(Void.class).subscribe();
								return respuesta;
							});
				}).switchIfEmpty(
					Mono.just(inscp).map(er -> {
					respuesta.put("Error", "El curso no tiene ninguna inscripción");
					return respuesta;
				}));
			}
		});
	}

	@Override
	public Mono<Map<String, Object>> deleteInscription(String id) {
		Map<String, Object> respuesta = new HashMap<String, Object>();

		return insRep.findById(id).map(insrDb -> {
			if(insrDb.getFamilyMembers() == null && insrDb.getStudents() == null) {
				respuesta.put("Mensaje: ", "La inscripcion se eliminó con éxito");
			}else {
				respuesta.put("Status", HttpStatus.BAD_REQUEST.value());
				respuesta.put("Error", "La inscripción no se pudo elimininar, contine studiantes o familiares registrados");
			}
			return respuesta;
		});
	}

	@Override
	public Flux<Inscription> findStudentPerson(String institute, String person) {
		return insRep.findByInstituteAndStudentsPersonIn(institute, person);
	}

	@Override
	public Flux<Inscription> findMembersPerson(String institute, String person) {
		return insRep.findByInstituteAndFamilyMembersPersonIn(institute, person);
	}
}
