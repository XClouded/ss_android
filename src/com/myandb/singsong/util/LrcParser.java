package com.myandb.singsong.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcParser {
	
	private static HashMap<Long, String> infos;
	private static HashMap<Long, String> types;
	private static List<Long> lrcTime;
	private static String line = null;
	private static Pattern p = Pattern.compile("\\[[0-9]{2}:[0-9]{2}\\.[0-9]{2}\\]\\[[F|M|D|G]{1}\\].*$");
	private static Matcher matcher;
	
	public static void read (File file) throws IOException {
		infos = new HashMap<Long, String>();
		types = new HashMap<Long, String>();
		lrcTime = new ArrayList<Long>();
		
		InputStream is = new FileInputStream(file);
		InputStreamReader reader = new InputStreamReader(is, "MS949");
		BufferedReader bReader = new BufferedReader(reader);
		
		try {
			while ((line = bReader.readLine()) != null) {
				getText(line);
				line = null;
			}
			bReader.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Collections.sort (lrcTime);
	}
	
	public static void getText(String t) {
		matcher = p.matcher(t);
		if (matcher.matches()) {
			String[] times= t.split("]");
			
			Long time = getTime(times[0].substring(1));
			
			lrcTime.add(time);
			
			if (times.length == 2) {
				if (times[1].contains("[")) {
					types.put(time, times[1].substring(1));
					infos.put(time, " ");
				} else {
					types.put(time, "D");
					infos.put(time, times[1]);
				}
			} else if (times.length == 3) {
				types.put(time, times[1].substring(1));
				infos.put(time, times[2]);
			} else {
				types.put(time, "D");
				infos.put(time, "");
			}
		}
	}
	
	public static Long getTime (String t) {
		int m = 0;
		double s = 0;
		String[] times = t.split(":", 2);
		
		try {
			m = Integer.parseInt(times[0]);
			s = Double.parseDouble(times[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (long)(m * 1000 * 60 + s * 1000);
	}
	
	public static List<Long> getTimeset() {
		return lrcTime;
	}
	
	public static HashMap<Long, String> getInfos() {
		return infos;
	}
	
	public static HashMap<Long, String> getTypes() {
		return types;
	}
}
