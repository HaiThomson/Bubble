package source.module.market.vo;

import source.kernel.base.Base;

/**
 * @author Hai Thomson
 */
public class CommodityTest {
	public static void main(String[] args) {
		Base commodity = new Commodity();
		System.out.println(commodity.toString());
		System.out.println(commodity.call("sayName"));
	}
}
