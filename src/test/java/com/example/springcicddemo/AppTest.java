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
}