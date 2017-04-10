package source.module.market.vo;

/**
 * @author Hai Thomson
 */
public class GoodsTest {
	public static void main(String[] args) {
		Goods goods = new Goods();
		// goods.setId("8277237232832");
		goods.setName("Apple 7 plus");
		goods.setOrderCount(3L);
		goods.setPrice(7666L);
		goods.setDescribe("Apple 7 plus");
		goods.setProperty("id", "9989923");
		System.out.println(goods.getProperty("id"));
		System.out.println(goods.canSetProperty("id"));
		System.out.println(goods.canGetProperty("id"));
		System.out.println(goods.toString());
	}
}
