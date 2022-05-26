package nix.nhrin.apichallenge.controller;

import nix.nhrin.apichallenge.entity.Patient;
import nix.nhrin.apichallenge.service.DeIdentificationPatientRecordService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PatientController {

    @Autowired
    private DeIdentificationPatientRecordService deIdentificationPatientRecordService;

    @ResponseBody
    @PostMapping("/deIdentification")
    public JSONObject deIdentificationPatientRecord (@RequestBody Patient patient) {
        return deIdentificationPatientRecordService.getDeIdentificationAllInfo(patient);
    }
}
