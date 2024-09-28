package org.cookieandkakao.babting.common.apiresponse;

public class ApiResponseBody {
    // 응답 성공 body
    public static class SuccessBody<D> {
        private String status;
        private String message;
        private D data;

        public SuccessBody(String status, String message, D data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public D getData() {
            return data;
        }
    }

    public static class FailureBody {
        // 응답 실패 body
        private String status;
        private String message;

        public FailureBody(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

    }

}
