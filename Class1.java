
public class Class1 implements Comparable<Class1>{
		public String term;
		public int freq;
		
		public Class1(String t, int f) {
			term = t; 
			freq = f;
		}
		
		@Override
		public int compareTo(Class1 o) {
			int f = o.freq;
			int temp = f;
			if (this.freq > temp) {
				return 1;
			} else if (this.freq == temp) {
				return 0;
			} else {
				return -1;
			}
		}
		
	}