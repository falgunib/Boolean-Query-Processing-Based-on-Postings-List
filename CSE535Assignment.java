import java.io.*;
import java.util.*;
import java.util.Collections;


public class CSE535Assignment {

	static LinkedList<Class1> getTopKList = new LinkedList<Class1>();
	static HashMap<String, LinkedList<Class2>> postingsMap =  new HashMap<String, LinkedList<Class2>>();
	static LinkedList<Class2> getPostingsList = new LinkedList<Class2>();
	static Scanner scanner = new Scanner(System.in);
	static BufferedWriter bw;
	public static void main(String[] args) {
		
		String thisLine=null;
		int i;
		String t1=null, f1=null;
        
		try{
			BufferedReader br = new BufferedReader(new FileReader(args[0]));//"/Users/falgunibharadwaj/Downloads/term.idx"));//args[0]
			bw = new BufferedWriter(new FileWriter(args[1]));
			BufferedReader qt = new BufferedReader(new FileReader(args[3]));
			int k = Integer.parseInt(args[2]);
			
			while ((thisLine = br.readLine()) != null) { // while loop for postings hashmap
	        
				LinkedList<Class2> postingsList = new LinkedList<Class2>();
				String[] parts = thisLine.split("\\\\");
				String doc1=null, occ1=null;
				t1 = parts[0];
				f1 = parts[1].replace("c","");
				parts[2] = parts[2].replace("m[", "");
				parts[2] = parts[2].replace(" ", "");
				parts[2] = parts[2].replace("]", "");
				int f = Integer.parseInt(f1);
				String[] postings = parts[2].split(",");
				
				for(i=0;i<f;i++) {				
					String[] postingsParts = postings[i].split("/");
					doc1 = postingsParts[0];
					occ1 = postingsParts[1];
					postingsList.add(new Class2(doc1,occ1));
				}
			
				getTopKList.add(new Class1(t1, f));
				postingsMap.put(t1,postingsList);
	
			}//end of while loop
			getTopK(k);
			while((thisLine = qt.readLine()) != null) {//while loop to call individual functions
				String[] qTerms = thisLine.split(" ");
				int m;
				for (m = 0; m< qTerms.length; m++)  
					getPostings(qTerms[m]);
				taatAND(qTerms, m);
				taatOR(qTerms, m);
				daatAND(qTerms,m);
				daatOR(qTerms, m);
				
			}
			
			br.close();
			bw.close();
		} // end try
	    catch (IOException e) {
	      e.printStackTrace();
	    }
	}	
	
	public static void getTopK(int n) {
			Collections.sort(getTopKList, Collections.reverseOrder());
		    int k = 0, k1 = n;
		   try {
			bw.write("FUNCTION: getTopK "+ k1);
		    bw.newLine();
		    bw.write("Result: ");
			for(Class1 temp:getTopKList){
	        	k++;
	        	if(k<k1)
	        		bw.write(temp.term+", ");
	        	if(k==k1) bw.write(temp.term+" ");
	        }
		    } catch (IOException e) {
				e.printStackTrace();
			   }
	}
		
	public static void getPostings(String searchTerm) {	
        
        try {

            int i=0, n;
            bw.newLine();
            getPostingsList = postingsMap.get(searchTerm);
            if(getPostingsList == null) 
            	{bw.write("Term not found!"); return;}
        	
			bw.write("FUNCTION: getPostings "+ searchTerm);
			bw.newLine();
			bw.write("Ordered by doc IDs: ");
			n = getPostingsList.size();
			n--;
			for(Class2 temp:getPostingsList){
        		if(i<n){
        			bw.write(temp.doc + ", ");
        			i++;
				}
        		else
        			bw.write(temp.doc + " ");
			}

			bw.newLine();
			bw.write("Ordered by TF: ");
			Collections.sort(getPostingsList, Collections.reverseOrder());
			i=0;
			for(Class2 temp:getPostingsList){
        		if(i<n){
        			bw.write(temp.doc + ", ");
        			i++;
				}
        		else
        			bw.write(temp.doc + " ");
			}
        } catch (IOException e) {
        	e.printStackTrace();
       	}
	}	
	
	public static void taatAND(String[] qTerms, int m) {
		
		long start = System.currentTimeMillis();
		ArrayList<String> queryTerms = new ArrayList<String>(); 
		ArrayList<String> docList = new ArrayList<String>();
		ArrayList<String> docList2 = new ArrayList<String>();
		LinkedList<Class2> demo = new LinkedList<Class2>();
		try {
		int comp = 0;
		for(int a1=0;a1<m;a1++){
			//System.out.println("Enter query terms: ");
			queryTerms.add(qTerms[a1]);
		}
		demo = postingsMap.get(queryTerms.get(0));
		if (demo==null) {
			bw.newLine();
			bw.write(queryTerms.get(0) + " Term not present in index.");
			return;
		}
		for(Class2 temp:demo){
			docList.add(temp.doc);
		}
		
		demo=null;
		for(int i = 1; i <queryTerms.size(); i++) {
			docList2.clear();
			demo = postingsMap.get(queryTerms.get(i));
			if (demo==null) {
				bw.newLine();
				bw.write(queryTerms.get(i) + " Term not present in index.");
				return;
			}
			for(Class2 temp:demo) {
				for(String val: docList) {
					if (val.equals(temp.doc)) { //check if each docID is present in 2 lists, if yes add to 3rd list
						docList2.add(temp.doc);
					}
					comp++;
				}
						
			}
			docList.clear();
			for(String val: docList2)
				docList.add(val);
		}
		long end = System.currentTimeMillis();
		long time = end-start;
		
			bw.newLine();
		
		bw.write("FUNCTION: termAtATimeQueryAnd ");
		int i;
		for(i=0; i<queryTerms.size()-1; i++){
			bw.write(queryTerms.get(i)+", "); 
		}
		bw.write(queryTerms.get(i)+" "); 
		bw.newLine();
		bw.write(docList.size() + " documents found.");
		bw.newLine();
		bw.write(comp + " comparisons made.");
		bw.newLine();
		bw.write(time + " miliseconds used.");
		bw.newLine();
		bw.write("Result: ");
		int n = docList.size();
		if (n == 0) bw.write("Terms not found!");
		n--;
		i=0;
		for(String temp:docList){
    		if(i<n){
    			bw.write(temp + ", ");
    			i++;
			}
    		else
    			bw.write(temp + " ");
    		
		}
		bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
		
	public static void taatOR(String[] qTerms, int m) {
		
		long start = System.currentTimeMillis();
		ArrayList<String> queryTerms = new ArrayList<String>(); 
		ArrayList<String> docList = new ArrayList<String>(); 
		LinkedList<Class2> demo = new LinkedList<Class2>();
		LinkedList<Class2> demo2 = new LinkedList<Class2>();
		try {
			int comp = 0;
		
		for(int a1=0;a1<m;a1++){
			queryTerms.add(qTerms[a1]);
		}
		demo = postingsMap.get(queryTerms.get(0));
		if (demo==null) {
			bw.newLine();
			bw.write(queryTerms.get(0) + " Term not present in index.");
			queryTerms.remove(0);
		}
		int flag=-1;
		for(int i = 1; i <queryTerms.size(); i++) {
			
			demo2 = postingsMap.get(queryTerms.get(i));
			if (demo2==null) {
				bw.newLine();
				bw.write(queryTerms.get(0) + " Term not present in index.");
				queryTerms.remove(i);break;
			}
			for(Class2 temp:demo2) {
				flag = -1;
				for(Class2 val: demo) {
					if ((val.doc).equals(temp.doc)) {// if elements of both lists are equal, break out of loop
						flag = 1;
						break;
					}
					comp++;	
				}
				if (flag == -1) {
						demo.add(temp);
					}
				
				}
						
			}
		for(Class2 temp:demo){
			docList.add(temp.doc);
		}
		Collections.sort(docList);
		long end = System.currentTimeMillis();
		long time = end-start;
		bw.newLine();
		bw.write("FUNCTION: termAtATimeQueryOr ");
		int i, n;
		for(i=0; i<queryTerms.size()-1; i++){
			bw.write(queryTerms.get(i)+", "); 
		}
		bw.write(queryTerms.get(i)+" ");
		bw.newLine();
		bw.write(demo.size() + " documents found.");
		bw.newLine();
		bw.write(comp + " comparisons made.");
		bw.newLine();
		bw.write(time + " miliseconds used.");
		bw.newLine();
		if (demo.size() == 0) bw.write("Terms not found!");
		Collections.sort(demo, Collections.reverseOrder());
		i=0; n=docList.size()-1;
		bw.write("Result: ");
		for(String temp:docList){
    		if(i<n){
    			bw.write(temp + ", ");
    			i++;
			}
    		else
    			bw.write(temp+ " ");
		}
		
		} catch (IOException e) {
        	e.printStackTrace();
       	}
	}
	
	public static void daatAND(String[] qTerms, int n) {
		
		long start = System.currentTimeMillis();
		LinkedList<LinkedList<Class2>> daatList = new LinkedList<LinkedList<Class2>>();
		LinkedList<Class2> demo = new LinkedList<Class2>();
		ArrayList<String> queryTerms = new ArrayList<String>();
		ArrayList<Integer> pointers = new ArrayList<Integer>(); 
		ArrayList<Integer> size1 = new ArrayList<Integer>(); 
		int i = 0, flag = -1, count = 0, index = 0, item = 0; 
		try {
		for(i=0;i<n;i++){
			queryTerms.add(qTerms[i]);
			daatList.add(postingsMap.get(queryTerms.get(i)));
			pointers.add(0);
		}
		long max = Integer.parseInt(daatList.get(0).get(pointers.get(0)).doc);
		//Comparing first terms
		int min = daatList.get(0).size();
		int p = 0;
		for(int j = 0; j<n; j++) {
			size1.add(daatList.get(j).size());
			if(daatList.get(j).size() < min ) {
				min = daatList.get(j).size();
				p = j;
			}	
		}
		int stopOp = -1;
		
		while(pointers.get(p) < min) {	//while the shortest postingslist is not null 
			if(stopOp == 1) break;
			for(i=0;i<n;i++) { 
				if( Integer.parseInt(daatList.get(i).get(pointers.get(i)).doc) == max ) {
					flag = 0;
					count++;
				}
				else if( Integer.parseInt(daatList.get(i).get(pointers.get(i)).doc) != max ){
					flag = 1;
					count++;
					break;
				}
			}
			//adding to and list if doc IDs are equal
			if (flag == 0) {
				
				demo.add(daatList.get(0).get(pointers.get(0))); //add to new list
				for(int ki=0;ki<n;ki++) {
					item = pointers.get(ki);
					item++;
					if (ki == p && item>=min) { //check condition for pointers so that it does not go index out of bounds
						pointers.set(ki, item);
						stopOp = 1;
						break;
					}
					else if(ki !=p && item>=size1.get(ki)) { //check condition for pointers so that it does not go index out of bounds
						pointers.set(ki, item);
						stopOp = 1;
						break;
					}
					pointers.set(ki, item);
				}
			}
			else if (flag == 1) { //if elements not equal, then find max
				for(i=0;i<n;i++) {
					
					if( Integer.parseInt(daatList.get(i).get(pointers.get(i)).doc) > max ) { //finding max
						max = Integer.parseInt(daatList.get(i).get(pointers.get(i)).doc);
						count++;
						index = i;
					}
				}
				
				for(i=0;i<n;i++) { //compare all other elements of other lists with max
					if( i != index) {
						item = 0;
						while (Integer.parseInt(daatList.get(i).get(pointers.get(i)).doc) < max ) {
							item = pointers.get(i); //keep moving other pointers till their value become equal to or greater thann max
							item++;

							count++;
							if (i == p && item>=min) { //check condition for pointers so that it does not go index out of bounds
								pointers.set(i, item);
								stopOp = 1;
								break;
							}
							else if(i !=p && item>=size1.get(i)) { //check condition for pointers so that it does not go index out of bounds
								pointers.set(i, item);
								stopOp = 1;
								break;
							}
							pointers.set(i, item);
						}
					}
				}
			}
		}
		long end = System.currentTimeMillis();
		long time = end - start;
		bw.newLine();
		bw.write("FUNCTION: docAtATimeQueryAnd ");
		for(i=0; i<queryTerms.size()-1; i++){
			bw.write(queryTerms.get(i)+", "); 
		}
		bw.write(queryTerms.get(i)+" ");
		bw.newLine();
		bw.write(demo.size() + " documents found.");
		bw.newLine();
		bw.write(count + " comparisons made.");
		bw.newLine();
		bw.write(time + "miliseconds used.");
		bw.newLine();
		bw.write("Result: ");
		if (demo.size() == 0) bw.write("Terms not found!");
		int q;
		q=demo.size()-1;
		for(Class2 temp:demo){
    		if(p<q){
    			bw.write(temp.doc + ", ");
    			p++;
			}
    		else
    			bw.write(temp.doc + " ");
		}
		
		
	} catch (IOException e) {
    	e.printStackTrace();
   	}
		
	}

	public static void daatOR(String[] qTerms, int n) {
		
		long start = System.currentTimeMillis();
		LinkedList<LinkedList<Class2>> daatList = new LinkedList<LinkedList<Class2>>();
		LinkedList<Class2> demo = new LinkedList<Class2>();
		ArrayList<String> queryTerms = new ArrayList<String>();
		ArrayList<Integer> pointers = new ArrayList<Integer>(); 
		ArrayList<Integer> size1 = new ArrayList<Integer>(); 
		int i = 0, flag = -1, count = 0, index = 0, item = 0; 
		try{
		for(i=0;i<n;i++){
			queryTerms.add(qTerms[i]);
			daatList.add(postingsMap.get(queryTerms.get(i)));
			pointers.add(0);
		}
		long max = Integer.parseInt(daatList.get(0).get(pointers.get(0)).doc);
		//Comparing first terms
		int last = daatList.get(0).size();
		int p = 0;
		for(int j = 0; j<n; j++) {
			//daatList.get(j).add(new Class2("0000","0000"));
			size1.add(daatList.get(j).size());
			if(daatList.get(j).size() > last ) {
				last = daatList.get(j).size();
				p = j;
			}	
		}
		for(int j = 0; j<n; j++) {
			while(daatList.get(j).size()!=last) {
				daatList.get(j).add(new Class2("0000","0000"));
			}
		}
		int stopOp = -1;
		
		while(pointers.get(p) < last) {	
			if(stopOp == 1) break;
			for(i=0;i<n;i++) { 
				if( Integer.parseInt(daatList.get(i).get(pointers.get(i)).doc) == max ) {
					flag = 0;
					count++;
					//System.out.println("first if " + i);
					//break;
				}
				else if( Integer.parseInt(daatList.get(i).get(pointers.get(i)).doc) != max ){
					flag = 1;
					count++;
					//System.out.println("first else " + i);
					break;
				}
			}
			//adding to and list
			if (flag == 0) {
				//System.out.println("max "+max);
				//System.out.println("same file getting added "+ daatList.get(0).get(pointers.get(0)).doc);
				demo.add(daatList.get(0).get(pointers.get(0)));
				for(int ki=0;ki<n;ki++) {
					item = pointers.get(ki);
					item++;
					if (ki == p && item>=last) {
						pointers.set(ki, item);
						stopOp = 1;
						break;
					}
					else if(ki !=p && item>=size1.get(ki)) {
						pointers.set(ki, item);
						stopOp = 1;
						break;
					}
					pointers.set(ki, item);
				}
			}
			else if (flag == 1) {

				//System.out.println("second else ");
				for(i=0;i<n;i++) {
					if( Integer.parseInt(daatList.get(i).get(pointers.get(i)).doc) > max ) {
						max = Integer.parseInt(daatList.get(i).get(pointers.get(i)).doc);
						//System.out.println("max is "+ max);
						count++;
						index = i;
					}
				}
				//System.out.println(max);
				
				for(i=0;i<n;i++) {

					//System.out.println(i+" going into for loop "+n);
					if( i != index) {
						//item = 0;
						int present = 0;
						while (Integer.parseInt(daatList.get(i).get(pointers.get(i)).doc) < max ) {
							item = pointers.get(i);
							item++;
							//System.out.println(daatList.get(i).get(pointers.get(i)).doc);
							if(demo!=null)
								for (Class2 arr : demo) {
								if(arr == daatList.get(i).get(pointers.get(i))) {
					            	//count++;
					            	present = 1;
					            	break;
					            }
					         }
							
					        if (present != 1 && (Integer.parseInt(daatList.get(i).get(pointers.get(i)).doc) != 0) ) {
					       	   demo.add(daatList.get(i).get(pointers.get(i)));
					       	   //
					        }
							count++;

							//System.out.println("after count");
							if (i == p && item>=last) {
								pointers.set(i, item);
								stopOp = 1;
								break;
							}
							else if(i !=p && item>=size1.get(i)) {
								pointers.set(i, item);
								stopOp = 1;
								break;
							}
							pointers.set(i, item);
						}
					}
				}
			}
		}
		long end = System.currentTimeMillis();
		long time = end - start;
		bw.newLine();
		bw.newLine();
		bw.write("FUNCTION: docAtATimeQueryOr ");
		for(i=0; i<queryTerms.size()-1; i++){
			bw.write(queryTerms.get(i)+", "); 
		}
		bw.write(queryTerms.get(i)+" ");
		bw.newLine();
		bw.write(demo.size() + " documents found.");
		bw.newLine();
		bw.write(count + " comparisons made.");
		bw.newLine();
		bw.write(time + " miliseconds used.");
		bw.newLine();
		if (demo.size() == 0) bw.write("Terms not found!");
		int q;
		q=demo.size()-1;
		for(Class2 temp:demo){
    		if(p<q){
    			bw.write(temp.doc + ", ");
    			p++;
			}
    		else
    			bw.write(temp.doc + " ");
		}
		} catch (IOException e) {
	    	e.printStackTrace();
	   	}
		
	
	}

}


