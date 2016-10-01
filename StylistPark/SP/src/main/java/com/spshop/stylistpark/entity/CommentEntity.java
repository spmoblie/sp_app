package com.spshop.stylistpark.entity;

import java.util.List;

public class CommentEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 评论Id
	 */
	private String commentId;

	/**
	 * 头像
	 */
	private String headImg;

	/**
	 * 昵称
	 */
	private String userNick;

	/**
	 * 内容
	 */
	private String content;

	/**
	 * 时间
	 */
	private String addTime;

	/**
	 * 传输数据集
	 */
	private List<CommentEntity> mainLists;


	public CommentEntity() {
		super();
	}


	public CommentEntity(int errCode, String errInfo) {
		super(errCode, errInfo);
	}

	@Override
	public String getEntityId() {
		return commentId;
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}

	public String getUserNick() {
		return userNick;
	}

	public void setUserNick(String userNick) {
		this.userNick = userNick;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public List<CommentEntity> getMainLists() {
		return mainLists;
	}

	public void setMainLists(List<CommentEntity> mainLists) {
		this.mainLists = mainLists;
	}
}
