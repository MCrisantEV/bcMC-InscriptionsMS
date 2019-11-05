package mc.bc.ms.inscriptions.app.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mc.bc.ms.inscriptions.app.models.Inscription;
import mc.bc.ms.inscriptions.app.services.InscriptionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/inscriptions")
public class InscriptionController {

	@Autowired
	private InscriptionService insServ;

	@PostMapping
	public Mono<Map<String, Object>> createInscription(@RequestBody Inscription inscription) {
		return insServ.saveInscription(inscription);
	}
	
	@GetMapping
	public Flux<Inscription> listInscription(){
		return insServ.findAllInscription();
	}
	
	@GetMapping("/{id}")
	public Mono<Inscription> findIdInscription(@PathVariable String id){
		return insServ.findIdInscription(id);
	}
	
	@PutMapping("/{id}")
	public Mono<Map<String, Object>> editInscription(@PathVariable String id, @RequestBody Inscription inscription) {
		return insServ.updateInscription(id, inscription);
	}
	
	@DeleteMapping("/{id}")
	public Mono<Map<String, Object>> removeCourse(@PathVariable String id) {
		return insServ.deleteInscription(id);
	}
	
	@GetMapping("/students/{id}/{institute}")
	public Flux<Inscription> findStudents(@PathVariable String id, @PathVariable String institute){
		return insServ.findStudentPerson(institute, id);
	}
	
	@GetMapping("/members/{id}/{institute}")
	public Flux<Inscription> findFamilyMembers(@PathVariable String id, @PathVariable String institute){
		return insServ.findMembersPerson(institute, id);
	}

}
