package com.todoapp.shared_todo;

import com.todoapp.shared_todo.dto.Users;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SharedTodoApplication {

	public static void main(String[] args) {
        SpringApplication.run(SharedTodoApplication.class, args);
        System.out.println("\n" +
                "=================================================\n" +
                "🚀 Shared ToDo Application 시작 완료!\n" +
                "=================================================\n" +
                "📋 Swagger UI: http://localhost:8080/swagger-ui/index.html\n" +
                "⭐ JPA Auditing 적용: 자동 시간/사용자 추적\n" +
                "=================================================\n");
    }
}
