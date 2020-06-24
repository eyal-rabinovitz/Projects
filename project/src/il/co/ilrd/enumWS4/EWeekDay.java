package il.co.ilrd.enumWS4;

public class EWeekDay {
	public enum WeekDay 
	{ 
		SUNDAY(1),
		MONDAY(2),
		TUESDAY(3),
		WEDNESDAY(4),
		THURSDAY(5),
		FRIDAY(6),
		SATURDAY(7);
		
	    private int day;
	    
	    private WeekDay(int value) {
	        this.day = value;
	    }	
	    
	    public int getValue() {
	        return day;
	    }
	    
	    public static WeekDay fromValue(int value) {
	        switch (value) {
			case 1:
				return SUNDAY;
			case 2:
				return MONDAY;
			case 3:
				return TUESDAY;
			case 4:
				return WEDNESDAY;
			case 5:
				return THURSDAY;
			case 6:
				return FRIDAY;
			case 7:
				return SATURDAY;
			default:
				return null;
			}
	    }
	    
	    public static void printAll() {
	    	for(WeekDay day : WeekDay.values()) {
	    		System.out.println(day.toString());
	    		}
	    }

	    public String toString() {
	    	return "day " + day + " is " + name(); 
		}
	} 
	
	public static void main (String args[]) {
		WeekDay day1 = WeekDay.SUNDAY;
		System.out.println(day1);
		System.out.println(day1.getValue());
		
		WeekDay day3 = WeekDay.TUESDAY;
		System.out.println(day3);
		System.out.println(day3.getValue());

		System.out.println(WeekDay.fromValue(7));
		
		WeekDay.printAll();
	}
}
