package com.soho.test.domain;

import com.soho.mybatis.crud.domain.IDEntity;

@SuppressWarnings("serial")
public class Article implements IDEntity<Long> {

	private Long id;
	/** 文章标题 */
	private String title;
	/** 一级栏目ID */
	private Long columnId;
	/** 二级栏目ID */
	private Long scolumnId;
	/** 正文 */
	private String content;
	/** 文章摘要 */
	private String summary;
	/** 文章关键词 */
	private String articleKeyword;
	/** 查看次数 */
	private Long visitTime;
	/** 是否发表：0表示未发表；1表示已发表 */
	private Long publish;
	/** 是否置顶：0表示不置顶 ；1表示置顶 */
	private Long stick;
	/** 封面url */
	private String coverUrl;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getColumnId() {
		return columnId;
	}

	public void setColumnId(Long columnId) {
		this.columnId = columnId;
	}

	public Long getScolumnId() {
		return scolumnId;
	}

	public void setScolumnId(Long scolumnId) {
		this.scolumnId = scolumnId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getArticleKeyword() {
		return articleKeyword;
	}

	public void setArticleKeyword(String articleKeyword) {
		this.articleKeyword = articleKeyword;
	}

	public Long getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(Long visitTime) {
		this.visitTime = visitTime;
	}

	public Long getPublish() {
		return publish;
	}

	public void setPublish(Long publish) {
		this.publish = publish;
	}

	public Long getStick() {
		return stick;
	}

	public void setStick(Long stick) {
		this.stick = stick;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

}
