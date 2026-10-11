package com.locafilmes.config;

import com.locafilmes.dto.common.ApiErrorDTO;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    public static final String ESQUEMA = "bearerAuth";

    @Bean
    public OpenAPI locaFilmesOpenAPI() {
        Components components = new Components().addSecuritySchemes(ESQUEMA,
                new SecurityScheme()
                        .name(ESQUEMA)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Token retornado por POST /api/auth/login. Informe somente o token, sem a palavra Bearer."));

        ModelConverters.getInstance().read(ApiErrorDTO.class)
                .forEach(components::addSchemas);

        return new OpenAPI()
                .info(new Info()
                        .title("LocaFilmes API")
                        .version("0.2.0")
                        .description("""
                                API REST de locadora de filmes.
                                Rotas públicas: POST /api/auth/login e POST /api/auth/register.
                                As demais exigem Authorization: Bearer <token>.
                                Perfis: ADMIN e CLIENTE."""))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA))
                .components(components);
    }

    @Bean
    public OperationCustomizer respostasPadrao() {
        return (operation, handlerMethod) -> {
            var responses = operation.getResponses();
            adicionar(responses, "400", "Dados inválidos");
            adicionar(responses, "401", "Não autenticado, token ausente ou credenciais inválidas");
            adicionar(responses, "403", "Perfil sem permissão para este recurso");
            adicionar(responses, "500", "Erro interno");

            boolean temId = Arrays.stream(handlerMethod.getMethodParameters())
                    .anyMatch(parametro -> parametro.hasParameterAnnotation(PathVariable.class));
            if (temId) {
                adicionar(responses, "404", "Recurso não encontrado");
            }

            boolean escrita = handlerMethod.hasMethodAnnotation(PostMapping.class)
                    || handlerMethod.hasMethodAnnotation(PutMapping.class)
                    || handlerMethod.hasMethodAnnotation(PatchMapping.class)
                    || handlerMethod.hasMethodAnnotation(DeleteMapping.class);
            if (escrita) {
                adicionar(responses, "409", "Registro vinculado a outros dados");
                adicionar(responses, "422", "Regra de negócio violada");
            }
            return operation;
        };
    }

    private void adicionar(io.swagger.v3.oas.models.responses.ApiResponses responses, String codigo, String descricao) {
        if (responses.containsKey(codigo)) {
            return;
        }
        Schema<?> schema = new Schema<>();
        schema.set$ref("#/components/schemas/ApiErrorDTO");
        responses.addApiResponse(codigo, new ApiResponse()
                .description(descricao)
                .content(new Content().addMediaType("application/json", new MediaType().schema(schema))));
    }
}
