package com.kjh.snsmanager.vo;

// VO도 toString이나 equals 같은 건 정의함
public class SimpleDate {
	public int year, month, week, day;
	
	// 디버그용
	@Override
	public String toString() {
		return "year: " + year + ", month: " + month + ", day: " + day;
	}
	
	public String toDayTitle() {
		String dayFormat = "%d년 %d월 %d일";		
		return String.format(dayFormat, year, month, day);
	}
	
	// 넷 중 하나라도 다른 경우 다른 날짜로 간주
	// 예를들어 6월 5일과 5월 5일은 같은 날이 아님
	public boolean dayEquals(SimpleDate prevDate) {
		return weekEquals(prevDate) && prevDate.day == day;
	}
	
	public boolean weekEquals(SimpleDate prevDate) {
		return monthEquals(prevDate) && prevDate.week == week;
	}
	
	public boolean monthEquals(SimpleDate prevDate) {
		return yearEquals(prevDate) && prevDate.month == month;
	}
	
	public boolean yearEquals(SimpleDate prevDate) {
		return prevDate.year == year;
	}
	
}