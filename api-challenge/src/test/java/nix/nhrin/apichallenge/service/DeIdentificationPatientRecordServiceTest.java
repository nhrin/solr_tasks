package nix.nhrin.apichallenge.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class DeIdentificationPatientRecordServiceTest {

    @Autowired
    private DeIdentificationPatientRecordService service;

    @Test
    public void testCalculateAge() {
        String age = service.calculateAge(LocalDate.of(1910, 5, 30));
        assertEquals(age, "90+");
    }

    @Test
    public void testGetDeIdentificationZipCode() {
        String deIdentificatedZipCode = service.getDeIdentificationZipCode("01002");
        assertEquals(deIdentificatedZipCode, "010XX");
    }

    @Test
    public void testGetDeIdentificationNotesField() {
        String notes = "ssn 123-45-6789 email test@test.com date 2020/05/05 phone +380500000000";
        String forComparing = "ssn XXX-XX-XXXX email ***email*** date 2020 year phone ***phone number***";
        String deIdentificatedNotes = service.getDeIdentificationNotesField(notes);
        assertEquals(deIdentificatedNotes, forComparing);
    }

}
