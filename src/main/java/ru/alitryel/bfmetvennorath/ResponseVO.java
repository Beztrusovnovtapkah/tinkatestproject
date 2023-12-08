package ru.alitryel.bfmetvennorath;

public class ResponseVO<T> {
    /**
     * Положение дел
     */
    private Integer code;
    /**
     * Новости
     */
    private String message;
    /**
     * Данные
     */
    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

