package org.cookieandkakao.babting.common.apiresponse;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseGenerator {
    // response가 없고 응당은 성공일 때
    public static ResponseEntity<ApiResponseBody.SuccessBody<Void>> success(HttpStatus status,
        String message) {
        return new ResponseEntity<>(
            new ApiResponseBody.SuccessBody<>(String.valueOf(status.value()), message, null),
            status);
    }

    // response가 있고 응답이 성공일 때
    public static <D> ResponseEntity<ApiResponseBody.SuccessBody<D>> success(HttpStatus status,
        String message, D data) {
        return new ResponseEntity<>(
            new ApiResponseBody.SuccessBody<>(String.valueOf(status.value()), message, data),
            status);
    }

    // 응답이 실패일 때
    public static ResponseEntity<ApiResponseBody.FailureBody> fail(HttpStatus status,
        String message) {
        return new ResponseEntity<>(
            new ApiResponseBody.FailureBody(String.valueOf(status), message), status);
    }
}
