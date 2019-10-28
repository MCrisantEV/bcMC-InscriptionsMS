package mc.bc.ms.inscriptions.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mc.bc.ms.inscriptions.app.services.InscriptionService;

@RestController
@RequestMapping("/inscriptions")
public class InscriptionController {
	
	@Autowired
	private InscriptionService insServ;

}
