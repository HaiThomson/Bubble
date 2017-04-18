package source.module.market.vo;

import source.kernel.base.Base;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Hai Thomson
 */
public class CommodityTest {
	public static void main(String[] args) {
		Base commodity = new Commodity();
		System.out.println(commodity.toString());
		try {
			System.out.println(commodity.call("sayName"));
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
