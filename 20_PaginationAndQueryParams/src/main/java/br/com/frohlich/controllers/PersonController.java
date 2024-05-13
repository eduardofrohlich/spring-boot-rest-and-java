package br.com.frohlich.controllers;

import br.com.frohlich.data.vo.v1.PersonVO;
import br.com.frohlich.services.PersonServices;
import br.com.frohlich.util.MediaType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/person/v1")
@Tag(name = "People", description = "API Endpoint for People Management")

public class PersonController {

    @Autowired
    private PersonServices service;

    @GetMapping(produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Find all people", description = "Find all people", tags = {"People"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema =
                                            @Schema(implementation = PersonVO.class))
                                    )
                            }),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal error"),
            }
    )
    public ResponseEntity<PagedModel<EntityModel<PersonVO>>> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                     @RequestParam(value = "size", defaultValue = "12") Integer size,
                                                                     @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName")); //1st models's attribute
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Finds a Person", description = "Finds a Person ", tags = {"People"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content =
                    @Content(schema = @Schema(implementation =
                            PersonVO.class))
                    ),

                    @ApiResponse(responseCode = "204", description = "No content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal error"),
            }
    )
    public PersonVO findById(@PathVariable(value = "id") Long id) throws Exception {
        return service.findById(id);
    }

    @CrossOrigin(origins = {"http://localhost:8080", "https://erudio.com.br"})
    @PostMapping(consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML}, produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Adds a Person", description = "Adds a Person ", tags = {"People"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content =
                    @Content(schema = @Schema(implementation =
                            PersonVO.class))
                    ),

                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal error"),
            }
    )
    public PersonVO create(@RequestBody PersonVO person) throws Exception {
        return service.create(person);
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML}, produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Updates a Person", description = "Updates a Person ",
            tags =
                    {"People"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Updated",
                            content =
                            @Content(schema = @Schema(implementation =
                                    PersonVO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal error"),
            }
    )
    public PersonVO update(@RequestBody PersonVO person) throws Exception {
        return service.update(person);
    }

    @PatchMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Disables a Person by id", description = "Disables a Person by id", tags = {"People"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content =
                    @Content(schema = @Schema(implementation =
                            PersonVO.class))
                    ),
                    @ApiResponse(responseCode = "204", description = "No content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal error"),
            }
    )
    public PersonVO disablePerson(@PathVariable(value = "id") Long id) throws Exception {
        return service.disablePerson(id);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Deletes a Person", description = "Deletes a Person ",
            tags = {"People"},
            responses = {
                    @ApiResponse(responseCode = "204", description = "No " +
                            "content", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Bad " +
                            "Request", content = @Content),
                    @ApiResponse(responseCode = "401", description =
                            "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not " +
                            "found", content = @Content),
                    @ApiResponse(responseCode = "500", description =
                            "Internal error", content = @Content),
            }
    )
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build(); //returns 204 code
    }

}
