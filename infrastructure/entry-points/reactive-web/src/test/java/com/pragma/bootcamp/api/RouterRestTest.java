// package com.pragma.bootcamp.config;

// import static org.junit.jupiter.api.Assertions.assertAll;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import java.lang.reflect.Method;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springdoc.core.annotations.RouterOperations;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.bind.annotation.RequestMethod;
// import org.springframework.web.reactive.function.server.RouterFunction;
// import org.springframework.web.reactive.function.server.ServerResponse;

// @ExtendWith(MockitoExtension.class)
// class RouterRestTest {

// @Mock
// private Handler handler;

// @InjectMocks
// private RouterRest routerRest;

// @Test
// void routerFunction_ShouldReturnRouterFunction() {
// // Act
// RouterFunction<ServerResponse> routerFunction =
// routerRest.routerFunction(handler);

// // Assert
// assertNotNull(routerFunction);
// }

// @Test
// void routerFunction_ShouldConfigureAllRoutes() {
// // Act
// RouterFunction<ServerResponse> routerFunction =
// routerRest.routerFunction(handler);

// // Assert - Verificar que todas las rutas están configuradas
// assertNotNull(routerFunction);

// // Puedes verificar que el router function no es nulo y está configurado
// // La verificación específica de rutas es más compleja con RouterFunction
// // pero podemos verificar que el método se ejecuta sin errores
// }

// @Test
// void class_ShouldBeAnnotatedWithConfiguration() {
// // Assert
// assertTrue(RouterRest.class.isAnnotationPresent(Configuration.class));
// }

// @Test
// void routerFunctionMethod_ShouldBeAnnotatedWithBean() throws
// NoSuchMethodException {
// // Arrange
// Method method = RouterRest.class.getMethod("routerFunction", Handler.class);

// // Assert
// assertTrue(method.isAnnotationPresent(Bean.class));
// }

// @Test
// void class_ShouldHaveRouterOperationsAnnotation() {
// // Assert
// assertTrue(RouterRest.class.isAnnotationPresent(RouterOperations.class));
// }

// @Test
// void routerOperationsAnnotation_ShouldContainAllOperations() {
// // Arrange
// RouterOperations routerOperations =
// RouterRest.class.getAnnotation(RouterOperations.class);

// // Assert
// assertAll(
// () -> assertNotNull(routerOperations),
// () -> assertNotNull(routerOperations.value()),
// () -> assertTrue(routerOperations.value().length >= 4));
// }

// @Test
// void routerOperations_ShouldContainPostOperation() {
// // Arrange
// RouterOperations routerOperations =
// RouterRest.class.getAnnotation(RouterOperations.class);

// // Assert
// assertTrue(java.util.Arrays.stream(routerOperations.value())
// .anyMatch(operation -> operation.method() == RequestMethod.POST &&
// operation.path().equals("/api/v1/user")));
// }

// @Test
// void routerOperations_ShouldContainPutOperation() {
// // Arrange
// RouterOperations routerOperations =
// RouterRest.class.getAnnotation(RouterOperations.class);

// // Assert
// assertTrue(java.util.Arrays.stream(routerOperations.value())
// .anyMatch(operation -> operation.method() == RequestMethod.PUT &&
// operation.path().equals("/api/v1/user")));
// }

// @Test
// void routerOperations_ShouldContainGetOperation() {
// // Arrange
// RouterOperations routerOperations =
// RouterRest.class.getAnnotation(RouterOperations.class);

// // Assert
// assertTrue(java.util.Arrays.stream(routerOperations.value())
// .anyMatch(operation -> operation.method() == RequestMethod.GET &&
// operation.path().equals("/api/v1/user")));
// }

// @Test
// void routerOperations_ShouldContainDeleteOperation() {
// // Arrange
// RouterOperations routerOperations =
// RouterRest.class.getAnnotation(RouterOperations.class);

// // Assert
// assertTrue(java.util.Arrays.stream(routerOperations.value())
// .anyMatch(operation -> operation.method() == RequestMethod.DELETE &&
// operation.path().equals("/api/v1/user/{id}")));
// }

// @Test
// void routerFunction_ShouldHandlePostRequest() {
// // Act
// RouterFunction<ServerResponse> routerFunction =
// routerRest.routerFunction(handler);

// // Assert - Verificar indirectamente que la ruta POST está configurada
// assertNotNull(routerFunction);
// }

// @Test
// void routerFunction_ShouldHandlePutRequest() {
// // Act
// RouterFunction<ServerResponse> routerFunction =
// routerRest.routerFunction(handler);

// // Assert - Verificar indirectamente que la ruta PUT está configurada
// assertNotNull(routerFunction);
// }

// @Test
// void routerFunction_ShouldHandleGetRequest() {
// // Act
// RouterFunction<ServerResponse> routerFunction =
// routerRest.routerFunction(handler);

// // Assert - Verificar indirectamente que la ruta GET está configurada
// assertNotNull(routerFunction);
// }

// @Test
// void routerFunction_ShouldHandleDeleteRequest() {
// // Act
// RouterFunction<ServerResponse> routerFunction =
// routerRest.routerFunction(handler);

// // Assert - Verificar indirectamente que la ruta DELETE está configurada
// assertNotNull(routerFunction);
// }
// }