package mu.semtech.poc.shacl.rest;

import mu.semtech.poc.shacl.rdf.ModelConverter;
import mu.semtech.poc.shacl.rdf.ShaclService;
import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static java.lang.System.currentTimeMillis;
import static mu.semtech.poc.shacl.rdf.ModelConverter.CONTENT_TYPE_TURTLE;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/filter")
@CrossOrigin("*")
public class FilterApi {
    private final ShaclService service;

    public FilterApi(ShaclService service) {
        this.service = service;
    }

    @PostMapping(produces = CONTENT_TYPE_TURTLE, consumes = CONTENT_TYPE_TURTLE)
    public ResponseEntity<String> filter(@RequestBody String dataModel) {
        Graph result = service.filter(dataModel, Lang.TURTLE);
        return ResponseEntity.ok(ModelConverter.toString(ModelFactory.createModelForGraph(result), Lang.TURTLE));
    }

    @PostMapping(value = "/file-with-shacl", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ByteArrayResource> filterFileWithShacl(@RequestPart("shapes") MultipartFile shapesFile,
                                                                 @RequestPart("data") MultipartFile dataFile) {
        Graph filter = service.filter(dataFile,shapesFile);
        return generateResponseFile(dataFile.getOriginalFilename(), filter);
    }

    @PostMapping(value = "/file", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ByteArrayResource> filterFile(@RequestPart("data") MultipartFile dataFile) {
        Graph filter = service.filter(dataFile);
        return generateResponseFile(dataFile.getOriginalFilename(), filter);
    }


    private ResponseEntity<ByteArrayResource> generateResponseFile(String fileName, Graph graph) {
        byte[] modelBytes = ModelConverter.toBytes(ModelFactory.createModelForGraph(graph), Lang.TURTLE);
        return ResponseEntity.ok()
                .header(CONTENT_TYPE, "text/turtle")
                .header(CONTENT_DISPOSITION, "attachment; filename=\"" + currentTimeMillis()+"_"+fileName + "\"")
                .body(new ByteArrayResource(modelBytes));
    }
}
