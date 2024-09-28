package org.cookieandkakao.babting.domain.food.dto;

public class ApiResponseDto {
    private int status;
    private String message;
    private Object data;

    public ApiResponseDto() {
    }

    public ApiResponseDto(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
