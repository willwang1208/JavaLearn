package org.whb.web.util;

public class StackTraceUtil {

	public static String getLocation(){
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		if(elements.length > 2){
			return buildStackTraceLineString(elements[2]);
		}
		return null;
	}
	
	public static void printLocation(){
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		if(elements.length > 2){
			System.out.println(buildStackTraceLineString(elements[2]));
		}
	}
	
	private static String buildStackTraceLineString(StackTraceElement element){
		StringBuffer buf = new StringBuffer();
		buf.append(" at ");
		buf.append(element.getClassName());
		buf.append("#");
		buf.append(element.getMethodName());
		buf.append(" line ");
		buf.append(element.getLineNumber());
		return buf.toString();
	}
	
	public static String[] getStackTrace(){
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		String[] result = new String[elements.length];
		int index = 0;
		for(StackTraceElement element : elements){
			result[index] = buildStackTraceLineString(element);
			index++;
			System.out.println(element.getClassName() + "#" + element.getMethodName() + " " + element.getLineNumber());
		}
		return result;
	}
	
	public static void printStackTrace(){
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		for(StackTraceElement element : elements){
			System.out.println(buildStackTraceLineString(element));
		}
	}
}
