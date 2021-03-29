package com.example.springcicddemo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    @Test
    @DisplayName("의도적으로 실패하는 테스트 케이스")
    void failedTest(){
        assertEquals(10, 10);
    }

    @Test
    @DisplayName("String format Test")
    void StringFormatTest(){
        String serviceName = "localhost";
        String serviceUri = "hello";

        String format = String.format("http://%s:8080/%s", serviceName, serviceUri);

        assertEquals("http://localhost:8080/hello", format);
    }
}