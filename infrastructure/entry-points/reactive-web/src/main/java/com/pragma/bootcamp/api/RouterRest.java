package com.pragma.bootcamp.api;

import com.pragma.bootcamp.api.dto.ErrorResponse;
import com.pragma.bootcamp.api.dto.UserCreateDTO;
import com.pragma.bootcamp.api.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(path = "/api/v1/user", produces = {
                    MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.POST, beanClass = Handler.class, beanMethod = "listenSaveUser", operation = @Operation(operationId = "listenSaveUser", summary = "Crear un nuevo usuario", tags = {
                    "User Management"}, requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = UserCreateDTO.class))), responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente", content = @Content(schema = @Schema(implementation = UserDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "Conflicto. El usuario con el documento o email ya existe.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })),
            @RouterOperation(path = "/api/v1/user", produces = {
                    MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.PUT, beanClass = Handler.class, beanMethod = "listenUpdateUser", operation = @Operation(operationId = "listenUpdateUser", summary = "Actualizar un usuario existente", tags = {
                    "User Management"}, requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = UserDTO.class))), responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente", content = @Content(schema = @Schema(implementation = UserDTO.class)))
            })),
            @RouterOperation(path = "/api/v1/user/{document}", produces = {
                    MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET, beanClass = Handler.class, beanMethod = "listenFindByDocument", operation = @Operation(operationId = "listenFindByDocument", summary = "Buscar un usuario por documento", tags = {
                    "User Management"}, parameters = {
                    @Parameter(name = "document", in = ParameterIn.PATH, required = true, description = "Documento del usuario", schema = @Schema(type = "string"))
            }, responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(schema = @Schema(implementation = UserDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
            })),
            @RouterOperation(path = "/api/v1/user", produces = {
                    MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET, beanClass = Handler.class, beanMethod = "listenGetAllUsers", operation = @Operation(operationId = "listenGetAllUsers", summary = "Obtener todos los usuarios", tags = {
                    "User Management"}, responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de usuarios", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDTO.class)))
            })),
            @RouterOperation(path = "/api/v1/user/{id}", produces = {
                    MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.DELETE, beanClass = Handler.class, beanMethod = "listenDeleteUser", operation = @Operation(operationId = "listenDeleteUser", summary = "Eliminar un usuario por ID", tags = {
                    "User Management"}, parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "ID del usuario a eliminar")
            }, responses = {
                    @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente")
            }))
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/user"), handler::listenSaveUser)
                .andRoute(PUT("/api/v1/user"), handler::listenUpdateUser)
                .andRoute(GET("/api/v1/user"), handler::listenGetAllUsers)
                .andRoute(DELETE("/api/v1/user/{id}"), handler::listenDeleteUser)
                .andRoute(GET("/api/v1/user/{document}"), handler::listenFindByDocument);
    }
}
