package com.example.demo.dto;

public record ResponseDTO<T>(Boolean error, T mensaje) {
}
