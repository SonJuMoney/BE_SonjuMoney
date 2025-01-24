package com.hana4.sonjumoney.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hana4.sonjumoney.dto.JwtTokenDto;
import com.hana4.sonjumoney.dto.request.SignInRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
@SecurityScheme(
	name = "JWT",
	type = SecuritySchemeType.HTTP,
	scheme = "bearer",
	bearerFormat = "JWT",
	description = "JWT 인증을 위해 'Bearer {token}' 형식으로 입력하세요."
)
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("Sonju Money")
				.version("1.0"))
			.addSecurityItem(new SecurityRequirement().addList("JWT"))
			.components(new Components().addSecuritySchemes("JWT",
				new io.swagger.v3.oas.models.security.SecurityScheme()
					.name("JWT")
					.type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JWT")
					.description("로그인 후 응답 받은 AccessToken을 'Bearer {token}' 형태로 입력하세요.")
			))
			.addSecurityItem(new SecurityRequirement().addList("JWT"));
	}

	@RestController
	@RequestMapping("/api/auth")
	@Tag(name = "인증", description = "로그인 관련 API")
	static class SwaggerAuthController {
		@Operation(
			summary = "로그인 API",
			description = "로그인 시 JWT 토큰을 반환합니다.\n\n" +
				"이 API는 Swagger에서 문서화만 제공하며, 실제 처리는 LoginFilter가 담당합니다."
		)
		@ApiResponses({
			@ApiResponse(responseCode = "200", description = "로그인 성공",
				content = @Content(schema = @Schema(implementation = JwtTokenDto.class))),
			@ApiResponse(responseCode = "401", description = "비밀번호가 일치하지 않습니다."),
			@ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다."),
			@ApiResponse(responseCode = "500", description = "오류가 발생하였습니다.")
		})
		@PostMapping("/sign-in")
		public void login(
			@RequestBody(description = "로그인 요청 정보", required = true,
				content = @Content(schema = @Schema(implementation = SignInRequest.class)))
			SignInRequest signInRequest
		) {
		}
	}
}
