package br.com.frohlich.controllers;

import br.com.frohlich.data.vo.v1.BookVO;
import br.com.frohlich.services.BookServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book/v1")
@Tag(name = "Books", description = "API Endpoint for Books Management")

public class BookController {

    @Autowired
    private BookServices service;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Find all books", description = "Find all books", tags = {"books"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema =
                                            @Schema(implementation = BookVO.class))
                                    )
                            }),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal error"),
            }
    )
    public List<BookVO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Finds a Book", description = "Finds a Book ", tags = {"books"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content =
                    @Content(schema = @Schema(implementation =
                            BookVO.class))
                    ),

                    @ApiResponse(responseCode = "204", description = "No content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal error"),
            }
    )
    public BookVO findById(@PathVariable(value = "id") Long id) throws Exception {
        return service.findById(id);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Adds a Book", description = "Adds a Book ", tags = {"books"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success", content =
                    @Content(schema = @Schema(implementation =
                            BookVO.class))
                    ),

                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal error"),
            }
    )
    public BookVO create(@RequestBody BookVO Book) throws Exception {
        return service.create(Book);
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Updates a Book", description = "Updates a Book ",
            tags =
                    {"books"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Updated",
                            content =
                            @Content(schema = @Schema(implementation =
                                    BookVO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal error"),
            }
    )
    public BookVO update(@RequestBody BookVO Book) throws Exception {
        return service.update(Book);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Deletes a Book", description = "Deletes a Book ",
            tags = {"books"},
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
        return ResponseEntity.noContent().build(); //return 204 code
    }

}
