package mu.semtech.poc.shacl.rest;

import mu.semtech.poc.shacl.rdf.ModelConverter;
import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static java.lang.System.currentTimeMillis;
import static mu.semtech.poc.shacl.rdf.ModelConverter.*;
import static mu.semtech.poc.shacl.rdf.ModelConverter.CONTENT_TYPE_TURTLE;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/compare")
@CrossOrigin("*")
public class ComparisonApi {

    @PostMapping(value = "/difference", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ByteArrayResource> diff(@RequestPart("first") MultipartFile first,
                                                        @RequestPart("second") MultipartFile second) throws IOException {
        Model firstModel = toModel(first.getInputStream(), filenameToLang(first.getOriginalFilename(),Lang.TURTLE));
        Model secondModel = toModel(second.getInputStream(), filenameToLang(second.getOriginalFilename(),Lang.TURTLE));
        return generateResponseFile( "difference.ttl",difference(firstModel, secondModel));
    }

    @PostMapping(value = "/intersection", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ByteArrayResource> intersec(@RequestPart("first") MultipartFile first,
                                                        @RequestPart("second") MultipartFile second) throws IOException {
        Model firstModel = toModel(first.getInputStream(), filenameToLang(first.getOriginalFilename(),Lang.TURTLE));
        Model secondModel = toModel(second.getInputStream(), filenameToLang(second.getOriginalFilename(),Lang.TURTLE));
        return generateResponseFile( "intersection.ttl",intersection(firstModel, secondModel));
    }

    @PostMapping(value = "/equals", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map.Entry<String, Boolean>> equality(@RequestPart("first") MultipartFile first,
                                                              @RequestPart("second") MultipartFile second) throws IOException {
        Model firstModel = toModel(first.getInputStream(), filenameToLang(first.getOriginalFilename(),Lang.TURTLE));
        Model secondModel = toModel(second.getInputStream(), filenameToLang(second.getOriginalFilename(),Lang.TURTLE));

        return ResponseEntity.ok(Map.entry("result",ModelConverter.equals(firstModel, secondModel)));
    }

    private ResponseEntity<ByteArrayResource> generateResponseFile(String fileName, Model model) {
        byte[] modelBytes = ModelConverter.toBytes(model, Lang.TURTLE);
        return ResponseEntity.ok()
                .header(CONTENT_TYPE, "text/turtle")
                .header(CONTENT_DISPOSITION, "attachment; filename=\"" + currentTimeMillis()+"_"+fileName + "\"")
                .body(new ByteArrayResource(modelBytes));
    }
}
