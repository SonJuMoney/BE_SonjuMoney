package com.hana4.sonjumoney.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Children", description = "내 아이 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/children")
public class ChildController {

}
