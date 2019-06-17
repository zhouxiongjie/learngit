package com.shuangling.software.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class SearchHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String historyString;
    @NotNull
    private String createTime;
    @Generated(hash = 1448158937)
    public SearchHistory(Long id, @NotNull String historyString,
            @NotNull String createTime) {
        this.id = id;
        this.historyString = historyString;
        this.createTime = createTime;
    }
    @Generated(hash = 1905904755)
    public SearchHistory() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getHistoryString() {
        return this.historyString;
    }
    public void setHistoryString(String historyString) {
        this.historyString = historyString;
    }
    public String getCreateTime() {
        return this.createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }



}
