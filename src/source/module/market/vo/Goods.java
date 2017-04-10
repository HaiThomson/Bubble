package source.module.market.vo;

import source.kernel.base.BaseBean;
import source.kernel.security.validate.Validation;

/**
 * @author Hai Thomson
 */
public class Goods extends BaseBean implements Validation {
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public Long getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(Long orderCount) {
		this.orderCount = orderCount;
	}

	private String id = null;
	private String name = null;
	private String describe = null;
	private Long   price = null;
	private Long   orderCount = null;

	@Override
	public boolean validate() {
		// 限购！限购数可由DB取出
		if (orderCount != null && orderCount > 10) {
			return false;
		} else {
			return true;
		}
	}
}
