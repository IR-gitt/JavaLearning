package com;

import java.util.Objects;

public class Response {
    int cows;
    int bulls;

    public Response(int cows, int bulls) {
        this.cows = cows;
        this.bulls = bulls;
    }

    public int getСows() {
        return cows;
    }

    public int getBulls() {
        return bulls;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response response = (Response) o;
        return cows == response.cows && bulls == response.bulls;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cows, bulls);
    }

    @Override
    public String toString() {
        return "Response{" +
                "cows=" + cows +
                ", bulls=" + bulls +
                '}';
    }
}
