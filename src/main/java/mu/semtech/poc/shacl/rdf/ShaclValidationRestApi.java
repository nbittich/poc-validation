package mu.semtech.poc.shacl.rdf;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.shacl.ValidationReport;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@CrossOrigin("*")
public class ShaclValidationRestApi {
    private final ShaclValidationService service;

    public ShaclValidationRestApi(ShaclValidationService service) {
        this.service = service;
    }

    @PostMapping(value = "/validate", produces = "text/turtle", consumes = "text/turtle")
    public ResponseEntity<String> validate(@RequestBody String dataModel) {
        ValidationReport report = service.validate(dataModel, Lang.TURTLE);
        return ResponseEntity.ok(ModelConverter.toString(report.getModel(), Lang.TURTLE));
    }

    @PostMapping(value = "/validate-file-with-shacl", produces = "text/turtle", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> validateFileWithShacl(@RequestPart("shapes") MultipartFile shapesFile,
                                                @RequestPart("data") MultipartFile dataFile) {
        ValidationReport report = service.validate(shapesFile, dataFile);
        return ResponseEntity.ok(ModelConverter.toString(report.getModel(), Lang.TURTLE));
    }

    @PostMapping(value = "/validate-file", produces = "text/turtle", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> validateFile(@RequestPart("data") MultipartFile dataFile) {
        ValidationReport report = service.validate(dataFile);
        return ResponseEntity.ok(ModelConverter.toString(report.getModel(), Lang.TURTLE));
    }

    @PostMapping(value = "/filter", produces = "text/turtle", consumes = "text/turtle")
    public ResponseEntity<String> filter(@RequestBody String dataModel) {
        Graph result = service.filter(dataModel, Lang.TURTLE);
        return ResponseEntity.ok(ModelConverter.toString(ModelFactory.createModelForGraph(result), Lang.TURTLE));
    }

    @PostMapping(value = "/filter-file-with-shacl", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ByteArrayResource> filterFileWithShacl(@RequestPart("shapes") MultipartFile shapesFile,
                                                                   @RequestPart("data") MultipartFile dataFile) {
        Graph filter = service.filter(dataFile,shapesFile);
        byte[] modelBytes = ModelConverter.toBytes(ModelFactory.createModelForGraph(filter), Lang.TURTLE);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/turtle")
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + System.currentTimeMillis()+"_"+dataFile.getOriginalFilename() + "\"")
                .body(new ByteArrayResource(modelBytes));
    }

    @PostMapping(value = "/filter-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ByteArrayResource> filterFile(@RequestPart("data") MultipartFile dataFile) {
        Graph filter = service.filter(dataFile);
        byte[] modelBytes = ModelConverter.toBytes(ModelFactory.createModelForGraph(filter), Lang.TURTLE);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/turtle")
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + System.currentTimeMillis()+"_"+dataFile.getOriginalFilename() + "\"")
                .body(new ByteArrayResource(modelBytes));
    }

}
