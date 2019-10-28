package mc.bc.ms.inscriptions.app.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;

import mc.bc.ms.inscriptions.app.repositories.InscriptionRepository;
import mc.bc.ms.inscriptions.app.services.InscriptionService;

@Service
public class InscriptionImpl implements InscriptionService {
	
	@Autowired
	private InscriptionRepository insRep;
	
	@Autowired
	private Validator validator;
}
