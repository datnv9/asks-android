package com.ptit.asks.model;
import java.io.Serializable;

public class Search implements Serializable {
    public String QuestionTitle;
    public String AskBy;
    public String StartDate;
    public String EndDate;

    public  Search (String QuestionTitle, String AskBy, String StartDate,String EndDate){
        this.QuestionTitle = QuestionTitle;
        this.AskBy = AskBy;
        this.StartDate = StartDate;
        this.EndDate = EndDate;
    }

    public String getAskBy() {
        return AskBy;
    }

    public String getEndDate() {
        return EndDate;
    }

    public String getQuestionTitle() {
        return QuestionTitle;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setAskBy(String askBy) {
        AskBy = askBy;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public void setQuestionTitle(String questionTitle) {
        QuestionTitle = questionTitle;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }
}