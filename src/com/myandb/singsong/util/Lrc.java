package com.myandb.singsong.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lrc {
	
	public static final String FILE_CHARSET = "MS949";
	
	private List<Line> timeLines;
	
	public Lrc(File file) throws IOException {
		this.timeLines = readLines(file);
	}
	
	private List<Line> readLines(File file) throws IOException {
		List<Line> lines = new ArrayList<Line>();
		
		InputStream stream = new FileInputStream(file);
		InputStreamReader streamReader = new InputStreamReader(stream, FILE_CHARSET);
		BufferedReader reader = new BufferedReader(streamReader);
		
		try {
			String rawLine = null;
			while ((rawLine = reader.readLine()) != null) {
				Line line = new Line(rawLine);
				if (line.isTimeLined()) {
					lines.add(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			reader.close();
			streamReader.close();
			stream.close();
		}
		return lines;
	}
	
	public List<Line> getTimeLines() {
		return timeLines;
	}
	
	public static long convertStringToMilliSecond(String time) {
		int minute = 0;
		double second = 0;
		String[] times = time.split(":", 2);
		
		try {
			minute = Integer.parseInt(times[0]);
			second = Double.parseDouble(times[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (long) (minute * 1000 * 60 + second * 1000);
	}
	
	private static String removeBracket(String text) {
		return text.substring(1, text.length() - 1);
	}

	@Override
	public String toString() {
		String content = "";
		for (Line line : timeLines) {
			content += line.toString();
			content += "\n";
		}
		return content;
	}

	public static class Line {
		
		public enum Type {
			
			MALE,
			
			FEMALE,
			
			DUAL,
			
			HINT,
			
			GO,
			
			NULL;
			
			public static class Builder {
				
				public static Type build(String name) {
					if ("M".equals(name) || "m".equals(name)) {
						return Type.MALE;
					} else if ("F".equals(name) || "f".equals(name)) {
						return Type.FEMALE;
					} else if ("D".equals(name) || "d".equals(name)) {
						return Type.DUAL;
					} else if ("G".equals(name) || "g".equals(name)) {
						return Type.GO;
					} else {
						return Type.HINT;
					}
				}
				
			}
			
		}
		
		private static Pattern timePattern = Pattern.compile("(\\[[0-9]{2}:[0-9]{2}\\.[0-9]{2}\\])(.*$)");
		private static Pattern typePattern = Pattern.compile("(\\[[F|M|D|G]{1}\\])(.*$)");
		
		private List<Word> timeWords;
		private Type type = Type.NULL;
		private long startTime;
		private boolean timelined;
		private boolean dynamicWords;
		
		public Line(String line) {
			timeWords = new ArrayList<Word>();
			parse(line);
		}
		
		private void parse(String line) {
			Matcher timeMatcher = timePattern.matcher(line);
			if (timeMatcher.find()) {
				timelined = true;
				String time = removeBracket(timeMatcher.group(1));
				startTime = convertStringToMilliSecond(time);
				timeWords = parseContent(timeMatcher.group(2));
			} else {
				timelined = false;
			}
		}
		
		private List<Word> parseContent(String content) {
			Matcher typeMatcher = typePattern.matcher(content);
			if (typeMatcher.find()) {
				String typeName = removeBracket(typeMatcher.group(1));
				type = Type.Builder.build(typeName);
				return parseWords(typeMatcher.group(2));
			} else {
				type = Type.HINT;
				return parseWords(content);
			}
		}
		
		private List<Word> parseWords(String contentOrTimeWords) {
			List<Word> words = new ArrayList<Word>();
			if (contentOrTimeWords.matches(Word.timePattern.pattern())) {
				dynamicWords = true;
				Matcher timeWordMatcher = Word.timePattern.matcher(contentOrTimeWords);
				while (timeWordMatcher.find()) {
					words.add(new Word(timeWordMatcher.group()));
				}
			} else {
				dynamicWords = false;
				words.add(new Word(contentOrTimeWords));
			}
			return words;
		}
		
		public boolean isTimeLined() {
			return timelined;
		}
		
		public boolean isDynamicWords() {
			return dynamicWords;
		}
		
		public Type getType() {
			return type;
		}
		
		public List<Word> getTimeWords() {
			return timeWords;
		}
		
		public long getStartTime() {
			return startTime;
		}

		@Override
		public String toString() {
			String content = "";
			for (Word word : timeWords) {
				content += word.toString();
			}
			return content;
		}
		
	}
	
	public static class Word {
		
		public static Pattern timePattern = Pattern.compile("(<[0-9]{2}:[0-9]{2}.[0-9]{2}>)(.*?)(<[0-9]{2}:[0-9]{2}.[0-9]{2}>)");
		
		private String content;
		private long startTime;
		private long endTime;
		
		public Word(String content) {
			Matcher matcher = timePattern.matcher(content);
			if (matcher.find()) {
				String start = removeBracket(matcher.group(1));
				startTime = convertStringToMilliSecond(start);
				
				this.content = matcher.group(2);
				
				String end = removeBracket(matcher.group(3));
				endTime = convertStringToMilliSecond(end);
			} else {
				this.content = content;
			}
		}
		
		public String getContent() {
			return content;
		}
		
		public long getStartTime() {
			return startTime;
		}
		
		public long getEndTime() {
			return endTime;
		}
		
		public long getDelay() {
			return endTime - startTime;
		}

		@Override
		public String toString() {
			return content; 
		}
		
	}
	
}
