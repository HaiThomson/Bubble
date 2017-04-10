/**
 * 版权所有 (c) 2017， 吕绪海. 保留所有权利
 * Copyright (c) 2017, Hai Thomson. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package source.kernel.ognl;

import source.kernel.Container;
import source.kernel.helper.ArraysHelper;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author Hai Thomson
 */
public class Ognl {
	public static Object getGlobal(String nl) {
		Map G = Container.app().Global;
		if (G == null || nl == null || nl.equals("")) {
			return null;
		}

		String[] nodes = nl.split("\\.");
		if (nodes == null) {
			return null;
		} else if (nodes.length == 1) {
			return G.get(nodes[0]);
		}

		return Ognl.get(G, nodes);
	}

	public static String getLang(String nl) {
		Map lang = (Map) Container.app().Global.get("lang");
		if (lang == null || nl == null || nl.equals("")) {
			return null;
		}

		String[] nodes = nl.split("\\.");
		if (nodes.length == 0) {
			return null;
		} else if (nodes.length == 1) {
			return (String) lang.get(nodes[0]);
		}

		return (String) Ognl.get(lang, nodes);
	}

	public static Object get(Object og, String nl) {
		if (og == null || nl == null || nl.equals("")) {
			return null;
		}

		String[] nodes = nl.split("\\.");
		if (nodes == null) {
			return null;
		} else if (nodes.length == 1) {
			return Ognl.get(og, new String[]{nodes[0]});
		}

		return Ognl.get(og, nodes);
	}

	private static Object get(Object og, String[] nl) {
		// System.out.println(nl.length);
		// System.out.println(og.toString());
		if (og instanceof  Map) {
			if (nl.length == 1) {
				return ((Map) og).get(nl[0]);
			} else {
				String[] nnl = ArraysHelper.split(nl, 1, nl.length - 1);
				if (nnl != null) {
					return get(((Map) og).get(nl[0]), nnl);
				}
			}
		}

		if (og instanceof List) {
			if (nl.length == 1) {
				return ((List) og).get(Integer.valueOf(nl[0]));
			} else {
				String[] nnl = ArraysHelper.split(nl, 1, nl.length - 1);
				if (nnl != null) {
					return get(((List) og).get(Integer.valueOf(nl[0])), nnl);
				}
			}
		}

		Field[] fields = og.getClass().getFields();
		if (fields != null && fields.length > 0) {
			for (int i = 0; i < fields.length; i++) {
				if (nl.length == 1) {
					try {
						if (fields[i].getName().equals(nl[0])) {
							return fields[i].get(og);
						}
					} catch (IllegalAccessException e) {
						return null;
					}
				} else {
					String[] nnl = ArraysHelper.split(nl, 1, nl.length - 1);
					if (nnl != null) {
						try {
							if (fields[i].getName().equals(nl[0])) {
								return get(fields[i].get(og), nnl);
							}
						} catch (IllegalAccessException e) {
							return null;
						}
					}
				}
			}
		}

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(og.getClass());
			PropertyDescriptor[] proDescrtptors = beanInfo.getPropertyDescriptors();
			if (proDescrtptors != null && proDescrtptors.length > 0) {
				for (PropertyDescriptor propDesc : proDescrtptors) {
					if (nl.length == 1) {
						Method readMethod = propDesc.getReadMethod();
						if (readMethod != null && readMethod.getName().equals(nl[0])) {
							return readMethod.getDefaultValue();
						}
					} else {
						String[] nnl = ArraysHelper.split(nl, 1, nl.length - 1);
						if (nnl != null) {
							Method readMethod = propDesc.getReadMethod();
							if (readMethod != null && readMethod.getName().equals(nl[0])) {
								return get(readMethod.getDefaultValue(), nnl);
							}
						}
					}
				}
			}
		} catch (IntrospectionException e) {
			return null;
		}
		return null;
	}
}
