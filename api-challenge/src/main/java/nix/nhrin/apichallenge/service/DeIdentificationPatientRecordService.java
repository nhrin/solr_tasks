package nix.nhrin.apichallenge.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.SneakyThrows;
import nix.nhrin.apichallenge.entity.Patient;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DeIdentificationPatientRecordService {

    private final static String EMAIL_REGEX = "[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+";
    private final static String US_SOCIAL_SECURITY_NUMBER_REGEX = "\\d{3}-?\\d{2}-?\\d{4}";
    private final static String US_TELEPHONE_NUMBER_REGEX = "(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}";
    private final static Map<String, Integer> DATE_REGEX = new HashMap<>() {{
        put("\\s+(?:19\\d{2}|20\\d{2})[-/.](?:0[1-9]|1[012])[-/.](?:0[1-9]|[12][0-9]|3[01])\\b", 1); //YYYY/MM/DD
        put("\\s+(?:0[1-9]|[12][0-9]|3[01])[-/.](?:0[1-9]|1[012])[-/.](?:19\\d{2}|20\\d{2})\\b", 7); //DD/MM/YYYY
        put("\\s+(?:0[1-9]|1[012])[-/.](?:0[1-9]|[12][0-9]|3[01])[-/.](?:19\\d{2}|20\\d{2})\\b", 7); //MM/DD/YYYY
    }};

    private final static Map<String, String> zipCodes = new HashMap<>();

    static {
        try (CSVReader csvReader = new CSVReader(new FileReader("src/main/resources/population_by_zcta_2010.csv"))) {
            String[] rowData;
            while ((rowData = csvReader.readNext()) != null) {
                zipCodes.put(rowData[0], rowData[1]);
            }
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject getDeIdentificationAllInfo(Patient patient) {
        JSONObject jsonObjectPatientInfo = new JSONObject();
        jsonObjectPatientInfo.put("age", calculateAge(patient.getBirthDate()));
        jsonObjectPatientInfo.put("admissionYear", patient.getAdmissionDate().getYear());
        jsonObjectPatientInfo.put("dischargeYear", patient.getDischargeDate().getYear());
        jsonObjectPatientInfo.put("notes", getDeIdentificationNotesField(patient.getNotes()));
        jsonObjectPatientInfo.put("zipCode", getDeIdentificationZipCode(patient.getZipCode()));
        return jsonObjectPatientInfo;
    }

    public String calculateAge(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        return age > 89 ? "90+" : String.valueOf(age);
    }

    public String getDeIdentificationNotesField(String notes) {
        String result = notes.replaceAll(EMAIL_REGEX, "***email***");
        result = result.replaceAll(US_TELEPHONE_NUMBER_REGEX, "***phone number***");
        result = result.replaceAll(US_SOCIAL_SECURITY_NUMBER_REGEX, "XXX-XX-XXXX");
        for (Map.Entry<String, Integer> entry : DATE_REGEX.entrySet()) {
            Pattern pattern = Pattern.compile(entry.getKey());
            Matcher matcher = pattern.matcher(result);
            if (matcher.find()) {
                String forReplace = matcher.group();
                String year = forReplace.substring(entry.getValue(), entry.getValue() + 4);
                result = result.replaceAll(forReplace, " " + year + " year");
            }
        }
        return result;
    }

    @SneakyThrows
    public String getDeIdentificationZipCode(String zipCode) {
        for (Map.Entry<String, String> rowData : zipCodes.entrySet()) {
            if (rowData.getKey().equals(zipCode)) {
                if (Integer.parseInt(rowData.getValue()) < 20000) {
                    return "00000";
                }
            }
        }
        return zipCode.substring(0, 3) + "XX";
    }
}
