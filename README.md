#Validation POC

This is a proof of concept for validation / filtering of rdf data based on shacl shapes.

This POC allows to validate / generate a report (in turtle) and filter invalid properties.

## HOW TO

- `cd example`
- Optionally, change the default application profile in `./example/config`
- `docker-compose up`

### Request examples:

#### 1. Input data

```
@prefix ex: <http://example.com/ns#> .
@prefix owl: <http://www.w3.org/2002/07/owl#> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

ex:Alice
    a ex:Person ;
    ex:ssn "987-65-432A" .

ex:Bob
    a ex:Person ;
    ex:ssn "123-45-6789" ;
    ex:ssn "124-35-6789" .

ex:Calvin
    a ex:Person ;
    ex:birthDate "1971-07-07"^^xsd:date ;
    ex:ssn "987-65-4321";
    ex:www "kekee";
    ex:worksFor ex:UntypedCompany .

ex:Momo
    a ex:Person ;
    ex:ssn "987-65-4321" .

```

#### 1. Validate (default application profile):

`POST http://localhost:8090/validate` 
`Content-Type: text/turtle`
`Body: (input data)`

#### 2. Validate file (default application profile):

`POST http://localhost:8090/validate-file` 
`Content-Type: multipart/form-data`
`Form data: data => data-to-validate.ttl`

#### 3. Validate file using a custom application profile file:

`POST http://localhost:8090/validate-file-with-shacl` 
`Content-Type: multipart/form-data`
`Form data: shapes=> shapes-file.ttl, data => data-to-validate.ttl`

#### 4. Filter (default application profile):

`POST http://localhost:8090/filter`
`Content-Type: text/turtle`
`Body: (input data)`

#### 5. Filter file (default application profile):

`POST http://localhost:8090/filter-file`
`Content-Type: multipart/form-data`
`Form data: data => data-to-filter.ttl`

#### 6. Filter file using a custom application profile file:

`POST http://localhost:8090/filter-file-with-shacl`
`Content-Type: multipart/form-data`
`Form data: shapes=> shapes-file.ttl, data => data-to-validate.ttl`
