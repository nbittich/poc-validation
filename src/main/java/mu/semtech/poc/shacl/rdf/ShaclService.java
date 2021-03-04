package mu.semtech.poc.shacl.rdf;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.engine.ShaclPaths;
import org.apache.jena.shacl.validation.ReportEntry;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.jena.vocabulary.RDF;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static mu.semtech.poc.shacl.rdf.ModelConverter.filenameToLang;
import static mu.semtech.poc.shacl.rdf.ModelConverter.toModel;

@Service
public class ShaclService {
  private final Shapes applicationProfile;

  public ShaclService(Shapes applicationProfile) {
    this.applicationProfile = applicationProfile;
  }

  @SneakyThrows
  public ValidationReport validate(MultipartFile shapesModel, MultipartFile dataModel) {
    return validate(dataModel.getInputStream(), filenameToLang(dataModel.getOriginalFilename()),
            shapesModel.getInputStream(), filenameToLang(shapesModel.getOriginalFilename()));
  }

  @SneakyThrows
  public ValidationReport validate(MultipartFile dataModel) {
    return validate(dataModel.getInputStream(), filenameToLang(dataModel.getOriginalFilename()));
  }

  public ValidationReport validate(InputStream dataModel, Lang modelLang) {
    return validate(toModel(dataModel, modelLang).getGraph());
  }

  public ValidationReport validate(String dataModel, String modelLang) {
    return validate(toModel(dataModel, modelLang).getGraph());
  }

  public ValidationReport validate(String dataModel, Lang modelLang) {
    return validate(toModel(dataModel, modelLang.getName()).getGraph());
  }

  public ValidationReport validate(InputStream dataModel, Lang modelLang, InputStream shapesModel, Lang shapesLang) {
    Graph shapesGraph = toModel(shapesModel, shapesLang).getGraph();
    Graph dataGraph = toModel(dataModel, modelLang).getGraph();
    Shapes shapes = Shapes.parse(shapesGraph);
    return ShaclValidator.get().validate(shapes, dataGraph);
  }

  public ValidationReport validate(Graph dataGraph) {
    return validate(dataGraph,applicationProfile);
  }

  public ValidationReport validate(Graph dataGraph, Shapes shapes) {
    return ShaclValidator.get().validate(shapes, dataGraph);
  }

  public Graph filter(InputStream dataModel, Lang modelLang, InputStream shapesModel, Lang shapesLang) {
    Graph dataGraph = toModel(dataModel, modelLang).getGraph();
    Graph shapesGraph = toModel(shapesModel, shapesLang).getGraph();
    Shapes shapes = Shapes.parse(shapesGraph);
    ValidationReport report = validate(dataGraph, shapes);
    return filter(dataGraph, shapes, report);
  }

  public Graph filter(InputStream dataModel, Lang modelLang) {
    Graph dataGraph = toModel(dataModel, modelLang).getGraph();
    ValidationReport report = validate(dataGraph);
    return filter(dataGraph, applicationProfile, report);
  }

  public Graph filter(Graph dataGraph, Shapes shapes, ValidationReport report) {
    List<String> targetClasses = shapes
            .getTargetShapes()
            .stream()
            .flatMap(s -> s.getTargets().stream().map(t -> t.getObject().getURI()))
            .collect(Collectors.toList());
    for (ReportEntry r : report.getEntries()) {
      dataGraph.remove(r.focusNode(),ShaclPaths.pathNode(r.resultPath()),null);
    }

    // filter the classes not defined as target shapes
    List<String> classesNotDefinedAsTargetShapes = dataGraph
            .find(null, RDF.type.asNode(), null)
            .filterDrop(triple -> targetClasses.contains(triple.getObject().getURI()))
            .mapWith(triple -> triple.getSubject().getURI()).toList();
    for (String sub : classesNotDefinedAsTargetShapes) {
      dataGraph.remove(NodeFactory.createURI(sub),null,null);
    }
    return dataGraph;
  }

  @SneakyThrows
  public Graph filter(String dataModel, Lang modelLang) {
    return filter(IOUtils.toInputStream(dataModel, StandardCharsets.UTF_8), modelLang);
  }

  @SneakyThrows
  public Graph filter(MultipartFile dataModel) {
    return filter(dataModel.getInputStream(), filenameToLang(dataModel.getOriginalFilename()));
  }

  @SneakyThrows
  public Graph filter(MultipartFile dataModel, MultipartFile shapesFile) {
    return filter(dataModel.getInputStream(), filenameToLang(dataModel.getOriginalFilename()),
            shapesFile.getInputStream(), filenameToLang(shapesFile.getOriginalFilename()));
  }


}
