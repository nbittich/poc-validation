package mu.semtech.poc.shacl.rdf;

import org.apache.jena.graph.Graph;
import org.apache.jena.shacl.Shapes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

import static mu.semtech.poc.shacl.rdf.ModelConverter.filenameToLang;

@Configuration
public class ApplicationConfig {
    @Value("${application-profile.default}")
    private Resource applicationProfile;

    @Bean
    public Shapes defaultApplicationProfile() throws IOException {
        Graph shapesGraph = ModelConverter.toModel(applicationProfile.getInputStream(), filenameToLang(applicationProfile.getFilename())).getGraph();
        return Shapes.parse(shapesGraph);
    }
}
