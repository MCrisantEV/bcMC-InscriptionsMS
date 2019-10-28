package mc.bc.ms.inscriptions.app.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mc.bc.ms.inscriptions.app.models.Inscription;
import mc.bc.ms.inscriptions.app.services.InscriptionService;
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

}
