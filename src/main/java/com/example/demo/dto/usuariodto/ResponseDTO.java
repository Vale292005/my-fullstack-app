package com.example.demo.dto.usuariodto;

public record ResponseDTO<T>(Boolean error, T mensaje) {
}
