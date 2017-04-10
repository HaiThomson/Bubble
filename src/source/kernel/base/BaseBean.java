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
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Hai Thomson
 */
public class BaseBean extends Base {

	/**
	 * 该方法并不能保证一定能设值成功
	 * @param propertyName
	 * @param value
	 * @throws IntrospectionException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public boolean setProperty(String propertyName, Object value) {
		if (propertyName == null || propertyName.equals("") || value == null) {
			return false;
		}

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
			PropertyDescriptor[] proDescrtptors = beanInfo.getPropertyDescriptors();
			if (proDescrtptors != null && proDescrtptors.length > 0) {
				for (PropertyDescriptor propDesc : proDescrtptors) {
					if (propDesc.getName().equals(propertyName)) {
						Method setMethod = propDesc.getWriteMethod();
						if (setMethod == null) {
							return false;
						}
						setMethod.invoke(this, value);
						return true;
					}
				}
			}
		}  catch (Exception e) {
			ExceptionHandler.handling(e, ExceptionHandler.EXCEPTION_SHOW_OFF, ExceptionHandler.EXCEPTION_LOG_OFF, ExceptionHandler.EXCEPTION_HALT_ON);
			return false;
		}
		return false;
	}

	public Object getProperty(String propertyName) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
			PropertyDescriptor[] proDescrtptors = beanInfo.getPropertyDescriptors();
			if (proDescrtptors != null && proDescrtptors.length > 0) {
				for (PropertyDescriptor propDesc : proDescrtptors) {
					if (propDesc.getName().equals(propertyName)) {
						Method getMethod = propDesc.getReadMethod();
						Object propertyObject = getMethod.invoke(this);
						return propertyObject;
					}
				}
			}
		} catch (Exception e) {
			ExceptionHandler.handling(e, ExceptionHandler.EXCEPTION_SHOW_OFF, ExceptionHandler.EXCEPTION_LOG_OFF, ExceptionHandler.EXCEPTION_HALT_ON);
			return null;
		}
		return null;
	}

	/**
	 * 这个一个相对低效率的方法，仅在需要时调用
	 * @return
	 */
	public String toString() {
		try {
			StringBuffer stringBuffer = new StringBuffer(this.getClass().getSimpleName());
			BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
			PropertyDescriptor[] proDescrtptors = beanInfo.getPropertyDescriptors();
			if (proDescrtptors != null && proDescrtptors.length - 1  > 0) {
				stringBuffer.append("[");
				for (PropertyDescriptor propDesc : proDescrtptors) {
					Method getMethod = propDesc.getReadMethod();
					Object propertyObject = getMethod.invoke(this);
					if (propertyObject == null) {
						stringBuffer.append(propDesc.getName());
						stringBuffer.append("=\"");
						stringBuffer.append("null");
						stringBuffer.append("\", ");
					} else {
						stringBuffer.append(propDesc.getName());
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
