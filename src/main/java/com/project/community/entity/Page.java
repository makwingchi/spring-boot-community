package com.project.community.entity;


/**
 * @author makwingchi
 * @Description Pagination related
 * @create 2020-04-15 14:48
 */
public class Page {
    // current page
    private int current = 1;
    // items every page
    private int limit = 10;
    // total items
    private int rows;
    // url
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * get the starting row of current page
     * @return
     */
    public int getOffset() {
        return limit * (current - 1);
    }

    /**
     * get total pages
     * @return
     */
    public int getTotal() {
        if(rows % limit == 0) {
            return rows/limit;
        }
        return rows/limit + 1;
    }

    /**
     * get starting page number
     * @return
     */
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * get ending page number
     * @return
     */
    public int getTo() {
        int to = current + 2;
        int total = this.getTotal();
        return to > total ? total : to;
    }
}
