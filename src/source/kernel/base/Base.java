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
package source.kernel.base;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Hai Thomson
 */
public abstract class Base {

	public boolean canGetProperty(String propertyName) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
			PropertyDescriptor[] proDescrtptors = beanInfo.getPropertyDescriptors();
			if (proDescrtptors != null && proDescrtptors.length > 0) {
				for (PropertyDescriptor propDesc : proDescrtptors) {
					if (propDesc.getName().equals(propertyName) && propDesc.getReadMethod() != null) {
						return true;
					}
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			ExceptionHandler.handling(e, ExceptionHandler.EXCEPTION_SHOW_OFF, ExceptionHandler.EXCEPTION_LOG_OFF, ExceptionHandler.EXCEPTION_HALT_ON);
			return false;
		}

		return false;
	}

	public boolean canSetProperty(String propertyName) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
			PropertyDescriptor[] proDescrtptors = beanInfo.getPropertyDescriptors();
			if (proDescrtptors != null && proDescrtptors.length > 0) {
				for(PropertyDescriptor propDesc : proDescrtptors){
					if(propDesc.getName().equals(propertyName) && propDesc.getWriteMethod() != null) {
						return true;
					}
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			ExceptionHandler.handling(e, ExceptionHandler.EXCEPTION_SHOW_OFF, ExceptionHandler.EXCEPTION_LOG_OFF, ExceptionHandler.EXCEPTION_HALT_ON);
			return false;
		}

		return false;
	}

	/**
	 * 为父类对象调用子类方法提供支持
	 * 为充血模型提供调用方法支持
	 * @param methodName 方法全名
	 * @return 运行结果
	 */
	public Object call(String methodName, Object... params) {
		try {
			Class subclass = this.getClass();
			for (Method method : subclass.getMethods()) {
				if (method != null && method.getName().equals(methodName)) {

					// 支持重载方法部分

					Object results = method.invoke(this, params);
					return results;
				}
			}
		} catch (Exception e) {
			ExceptionHandler.handling(e, ExceptionHandler.EXCEPTION_SHOW_OFF, ExceptionHandler.EXCEPTION_LOG_OFF, ExceptionHandler.EXCEPTION_HALT_OFF);
		}
		throw new RuntimeException(methodName + " is not found. check methodName and params or access authority");
	}

	/**
	 * 不支持"用类本身作为类公开属性"，这回使该方法陷入死循环直到堆栈崩溃.
	 * 这个一个相对低效率的方法，仅在需要时调用
	 * @return
	 */
	public String toString() {
		try {
			StringBuffer stringBuffer = new StringBuffer("");
			Class subclass = this.getClass();
			Field[] declaredFields = subclass.getFields();
			if (declaredFields != null && declaredFields.length > 0) {
				stringBuffer.append("[");
				for (Field field : declaredFields) {
					Object propertyObject = field.get(this);
					if (propertyObject == null) {
						stringBuffer.append(field.getName());
						stringBuffer.append("=\"");
						stringBuffer.append("null");
						stringBuffer.append("\", ");
					} else {
						stringBuffer.append(field.getName());
						stringBuffer.append("=\"");
						stringBuffer.append(propertyObject.toString());
						stringBuffer.append("\", ");
					}
				}
				stringBuffer.delete(stringBuffer.length() - 2, stringBuffer.length());
				stringBuffer.append("]");
				return stringBuffer.toString();
			} else {
				return super.toString();
			}
		} catch (Exception e) {
			ExceptionHandler.handling(e, ExceptionHandler.EXCEPTION_SHOW_OFF, ExceptionHandler.EXCEPTION_LOG_OFF, ExceptionHandler.EXCEPTION_HALT_ON);
			return super.toString();
		}
	}

}
