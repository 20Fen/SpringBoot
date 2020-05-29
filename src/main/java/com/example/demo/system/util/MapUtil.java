package com.example.demo.system.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import java.util.Map;

@Slf4j
public class MapUtil {
	public static <T> boolean isEmpty(Map<T,T> map){
		if(map == null || map.size() == 0){
			return true;
		}
		return false;
	}
	
	/**MAP 转bean
	 * @param map
	 * @param bean
	 * @return
	 */
	public static <T> T mapToBean(Map<String, ? extends Object> map,T bean){
		try {
			BeanUtils.populate(bean,map);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return bean;
	}
	
	/** bean转MAP
	 * @param bean
	 * @return
	 */
	public static <T> Map<String,String> beanToMap(T bean){
		try {
			return BeanUtils.describe(bean);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return null;
	}
}
