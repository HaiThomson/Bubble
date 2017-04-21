package source.module.economy.vo;

import source.kernel.base.BaseBean;

public class NewsJavaBean extends BaseBean {

	private int newsid;
	private int userid;
	private int kwid;
	private int hits;
	private String title;
	private String content;
	private String cdate;
	private String mdate;

	public NewsJavaBean() {

	}

	public NewsJavaBean(int newsid, int userid, int kwid, int hits, String title, String content, String cdate) {
		this.newsid = newsid;
		this.userid = userid;
		this.kwid = kwid;
		this.hits = hits;
		this.title = title;
		this.content = content;
		this.cdate = cdate;
	}

	public int getNewsid() {
		return newsid;
	}

	public void setNewsid(int newsid) {
		this.newsid = newsid;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public int getKwid() {
		return kwid;
	}

	public void setKwid(int kwid) {
		this.kwid = kwid;
	}

	public int getHits() {
		return hits;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCdate() {
		return cdate;
	}

	public void setCdate(String cdate) {
		this.cdate = cdate;
	}

	public String getMdate() {
		return mdate;
	}

	public void setMdate(String mdate) {
		this.mdate = mdate;
	}

}