package mu.semtech.poc.shacl.rdf;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.out.NodeFormatterTTL;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.lib.ShLib;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;


@SpringBootTest
@Slf4j
class ShaclValidationServiceTest {
    @Autowired
    private ShaclValidationService service;


    @Test
    void validate_default_ap_is_conform() throws IOException {
        InputStream validPerson = new ClassPathResource("validPerson.ttl").getInputStream();
        ValidationReport validate = service.validate(validPerson, Lang.TURTLE);
        Assertions.assertTrue(validate.conforms());
    }

    @Test
    void validate_default_ap_is_not_conform() throws IOException {
        InputStream validPerson = new ClassPathResource("person.ttl").getInputStream();
        ValidationReport report = service.validate(validPerson, Lang.TURTLE);
        Assertions.assertFalse(report.conforms());
        report.getModel().write(System.out,Lang.RDFJSON.getName());
    }

    @Test
    void filter() throws IOException {
        InputStream validPerson = new ClassPathResource("person.ttl").getInputStream();
        Graph g = service.filter(validPerson, Lang.TURTLE);
        ModelFactory.createModelForGraph(g).write(System.out, "TTL");
    }

    @Test
    void test(){
        log.error(ModelConverter.getContentType("TURTLE"));
    }
}
