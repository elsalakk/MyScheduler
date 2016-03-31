package business;

/**
 * Created by Khaled on 3/1/2016.
 */
//Stub Data Base
public class Sched {
    public Sched(){
        //nothing for now
    }
    public String[] getX(int x, int mode){
        String []retVal = null;
        String[] times = new String[]{"1:00","2:00","3:00","4:00","5:00","6:00","7:00","8:00","9:00","10:00","11:00","12:00"};

        if(x == 0){//0
            retVal = times;
        }
        else if(x == 1){
            retVal = new String[]{":00",":05",":10",":15",":20",":25",":30",":35",":40",":45",":50",":55"};
        }
        else if(x == 2){//1
            retVal = new String[] {"am","pm"};
        }
        else if(x == 3){//2
            if(mode == 24){
                mode = 0;
            }
            if(mode <= 12) {
                retVal = times;
            }
            else{
               String[] time = new String[12 - (12- (24-mode))];
               for(int z = (12- (24-mode)), y =0; z < times.length ; z++, y++){
                    time[y] = times[z];
               }
                retVal = time;
            }
        }
        else if(x == 4){
            retVal = new String[]{":00",":05",":10",":15",":20",":25",":30",":35",":40",":45",":50",":55"};
        }
        else if(x == 5){//3
            retVal = new String[] {"am","pm"};
        }
        else if(x == 6){//4
            retVal = new String[] {"Studying","Working","Class","Meeting","Gaming","Appointment","Date","More"};

        }
        else if(x == 7){//5
            retVal = new String[] {"Once","Once a month","Once a week","Every other day","Every Day"};
        }
        return retVal;
    }
    public float[] getY(int x, int mode){
        float[] times =new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        float []retVal = null;
        if(x == 0 || x == 1 || x ==4){
            retVal =times;
        }
        else if(x == 2){
            retVal =new float[]{1, 1};
        }
        else if(x == 3){
            if(mode <= 12) {

                retVal = times;
            }
            else{
                float[] time = new float[12 - (12- (24-mode))];
                for(int z = (12- (24-mode)), y =0; z < times.length ; z++, y++){
                    time[y] = times[z];
                }
                retVal = time;
            }
        }
        else if(x == 5){
            retVal =new float[]{1, 1};
        }
        else if(x == 6){
            retVal =new float[]{1, 1, 1, 1, 1, 1, 1, 1};
        }
        else if(x == 7){
            retVal =new float[]{1, 1, 1, 1, 1};
        }
        return retVal;
    }
}
