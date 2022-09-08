package io.github.helio.rest.controller;

import io.github.helio.domain.entity.Cliente;
import io.github.helio.domain.repository.Clientes;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Api("Api clientes")
public class ClienteController {

    @Autowired
    private Clientes RepositoryClientes;

    @GetMapping("{id}")
    @ApiOperation("Obter detalhes de um cliente")
    @ApiResponses(
            {
                    @ApiResponse(code = 200, message = "Cliente encontrado"),
                    @ApiResponse(code = 404, message = "Cliente não encontrado para o ID informado")
            }
    )
    public Cliente getClienteById(@PathVariable
                                      @ApiParam("ID do cliente")
                                      Integer id) {
        return RepositoryClientes
                .findById(id)
                .orElseThrow( () ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Cliente não encontrado"));
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Salvar novo cliente")
    @ApiResponses(
            {
                    @ApiResponse(code = 201, message = "Cliente salvo com sucesso"),
                    @ApiResponse(code = 404, message = "Erro de validação")
            }
    )
    public Cliente save( @RequestBody @Valid Cliente cliente) {
        return RepositoryClientes.save(cliente);
    }


    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
         RepositoryClientes.findById(id)
                 .map( cliente -> {
                     RepositoryClientes.delete(cliente);
                     return cliente;
                 })
                 .orElseThrow( () ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Cliente não encontrado"));
    }


    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update( @PathVariable Integer id,
                        @RequestBody @Valid Cliente cliente ){
         RepositoryClientes
                .findById(id)
                .map( clienteExistente -> {
                    cliente.setId(clienteExistente.getId());
                    RepositoryClientes.save(cliente);
                    return clienteExistente;
                }).orElseThrow( () ->
                         new ResponseStatusException(HttpStatus.NOT_FOUND,
                                 "Cliente não encontrado"));
    }

    @GetMapping
    public List<Cliente> find( Cliente filtro ) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher( ExampleMatcher.StringMatcher.CONTAINING );

        Example example = Example.of(filtro,  matcher);
        return RepositoryClientes.findAll(example);
    }
}
