package mu.semtech.poc.shacl.rest;

import mu.semtech.poc.shacl.rdf.ModelConverter;
import mu.semtech.poc.shacl.rdf.ShaclService;
import org.apache.jena.riot.Lang;
import org.apache.jena.shacl.ValidationReport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static mu.semtech.poc.shacl.rdf.ModelConverter.CONTENT_TYPE_TURTLE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/validate")
@CrossOrigin("*")
public class ValidationApi {
    private final ShaclService service;

    public ValidationApi(ShaclService service) {
        this.service = service;
    }

    @PostMapping(value = "/", produces = CONTENT_TYPE_TURTLE, consumes = CONTENT_TYPE_TURTLE)
    public ResponseEntity<String> validate(@RequestBody String dataModel) {
        ValidationReport report = service.validate(dataModel, Lang.TURTLE);
        return ResponseEntity.ok(ModelConverter.toString(report.getModel(), Lang.TURTLE));
    }

    @PostMapping(value = "/file-with-shacl", produces = CONTENT_TYPE_TURTLE, consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> validateFileWithShacl(@RequestPart("shapes") MultipartFile shapesFile,
                                                        @RequestPart("data") MultipartFile dataFile) {
        ValidationReport report = service.validate(shapesFile, dataFile);
        return ResponseEntity.ok(ModelConverter.toString(report.getModel(), Lang.TURTLE));
    }

    @PostMapping(value = "/file", produces = CONTENT_TYPE_TURTLE, consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> validateFile(@RequestPart("data") MultipartFile dataFile) {
        ValidationReport report = service.validate(dataFile);
        return ResponseEntity.ok(ModelConverter.toString(report.getModel(), Lang.TURTLE));
    }
}
