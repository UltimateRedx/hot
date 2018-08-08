package com.hotelpal.service.common.dto.response;

import java.util.List;

public class CommentListResponse {

    private Integer count;
    private Boolean hasMore;
    private List<CommentResponse> commentList;
    private List<CommentResponse> replyToCommentList;

    public Integer getCount() {
        return count;
    }
    public void setCount(Integer count) {
        this.count = count;
    }
    public Boolean getHasMore() {
        return hasMore;
    }
    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }
    public List<CommentResponse> getCommentList() {
        return commentList;
    }
    public void setCommentList(List<CommentResponse> commentList) {
        this.commentList = commentList;
    }
    public List<CommentResponse> getReplyToCommentList() {
        return replyToCommentList;
    }
    public void setReplyToCommentList(List<CommentResponse> replyToCommentList) {
        this.replyToCommentList = replyToCommentList;
    }
}
