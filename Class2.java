
public class Class2 implements Comparable<Class2>{
	public String doc;
	public String occ;

	public Class2(String oc, String dc) {
		doc = oc; 
		occ = dc;
	}
	
	@Override
	public int compareTo(Class2 o) {
		int temp = Integer.parseInt(o.occ);
		
		if (Integer.parseInt(this.occ) > temp) {
			return 1;
		} else if (Integer.parseInt(this.occ) == temp) {
			return 0;
		} else {
			return -1;
		}
	}
	
	
}
