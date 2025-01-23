package com.raifernando.lastfm;

public class Response {
    private String user;
    private int totalPages;
    private int page;
    private int total;

    @Override
    public String toString() {
        return "Response{" +
                "user=" + user +
                ", totalPages=" + totalPages +
                ", page=" + page +
                ", total=" + total +
                '}';
    }

    public String getUser() {
        return user;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getPage() {
        return page;
    }

    public int getTotal() {
        return total;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
