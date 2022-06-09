package uwaterloo;

import android.content.Context;
import ca.uwaterloo.clientdriversafety.R;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DataProcess {

    public static List str_to_list(String[] row)
    {
        List<Double> tmp = new ArrayList<>();
        row[1] = row[1].replaceAll("[\\[\\] \" ]", "" );
        row[row.length-1] = row[row.length-1].replaceAll("[\\[\\] \" ]", "" );
        for(int i=1;i<= row.length-1;i++)
        {
            tmp.add(Double.valueOf(row[i]));
        }
//        System.out.println(Arrays.toString(tmp.toArray()));
//        System.out.println(tmp.size());
        return tmp;
    }
    public static HashMap read_csv(Context context)
    {
        HashMap data = new HashMap<String, List<Double>>();
        InputStream inputStream = null;
        inputStream = context.getResources().openRawResource(R.raw.client_data);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                switch (row[0]) {
                    case "Accuracy": {
                        List tmp = str_to_list(row);
                        data.put("Accuracy", tmp);
                        break;
                    }
                    case "Bearing": {
                        List tmp = str_to_list(row);
                        data.put("Bearing", tmp);
                        break;
                    }
                    case "acceleration_x": {
                        List tmp = str_to_list(row);
                        data.put("acceleration_x", tmp);
                        break;
                    }
                    case "acceleration_y": {
                        List tmp = str_to_list(row);
                        data.put("acceleration_y", tmp);
                        break;
                    }
                    case "acceleration_z": {
                        List tmp = str_to_list(row);
                        data.put("acceleration_z", tmp);
                        break;
                    }
                    case "gyro_x": {
                        List tmp = str_to_list(row);
                        data.put("gyro_x", tmp);
                        break;
                    }
                    case "gyro_y": {
                        List tmp = str_to_list(row);
                        data.put("gyro_y", tmp);
                        break;
                    }
                    case "gyro_z": {
                        List tmp = str_to_list(row);
                        data.put("gyro_z", tmp);
                        break;
                    }
                    case "second": {
                        List tmp = str_to_list(row);
                        data.put("second", tmp);
                        break;
                    }
                    case "Speed": {
                        List tmp = str_to_list(row);
                        data.put("Speed", tmp);
                        break;
                    }
                }
                //System.out.println(Arrays.toString(row));
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return data;
    }

    public static List calc_real_value(List l_x,List l_y,List l_z)
    {
        List<Double> rst = new ArrayList<>();
        int size = l_x.size();
        for(int i=0;i<size;i++){
            double x= (double) l_x.get(i);
            double y= (double) l_y.get(i);
            double z= (double) l_z.get(i);
            double tmp = Math.sqrt(x*x+y*y+z*z);
            rst.add(tmp);
        }
        return rst;
    }

    public static List calc_rotate_x(List l_x,List l_y,List l_z)
    {
        List<Double> rst = new ArrayList<>();
        int size = l_x.size();
        for(int i=0;i<size;i++){
            double x= (double) l_x.get(i);
            double y= (double) l_y.get(i);
            double z= (double) l_z.get(i);
            double tmp = Math.sqrt(x*x+z*z);
            tmp = y/tmp;
            tmp = Math.atan(tmp);
            rst.add(tmp);
        }
        return rst;
    }

    public static List calc_rotate_y(List l_x,List l_y,List l_z)
    {
        List<Double> rst = new ArrayList<>();
        int size = l_x.size();
        for(int i=0;i<size;i++){
            double x= (double) l_x.get(i);
            double y= (double) l_y.get(i);
            double z= (double) l_z.get(i);
            double tmp = -x/z;
            tmp = Math.atan(tmp);
            rst.add(tmp);
        }
        return rst;
    }

    public static double getAverage(List<Double> input) {
        double sum = 0;
        int x = 0; //counting variable
        do {
            sum += input.get(x);
            x++;
        } while (x < input.size());
        double average = sum / x;
        return average;
    }

    public static double getMax(List<Double> input) {
        return input.stream().max(Double::compare).get();
    }

    public static double getMin(List<Double> input) {
        return input.stream().min(Double::compare).get();
    }

    public static double getIQR(List<Double> input) {
        DescriptiveStatistics da = new DescriptiveStatistics(input.stream().mapToDouble(Double::doubleValue).toArray());
        double iqr = da.getPercentile(75) - da.getPercentile(25);
        return iqr;
    }

    public static double get_max_consecutive_increase(List<Double> input)
    {
        double max_increase = 0;
        int count = 0;
        int size = input.size();
        for(int i=0;i<size-1;i++)
        {
            if(input.get(i+1)>input.get(i))
            {
                count++;
                if(count > max_increase)
                    max_increase = count;
            }else
            {
                count = 0;
            }
        }
        return (max_increase + 1) / size;
    }

    public static double get_max_consecutive_decrease(List<Double> input)
    {
        double max_decrease = 0;
        int count = 0;
        int size = input.size();
        for(int i=0;i<size-1;i++)
        {
            if(input.get(i+1)<input.get(i))
            {
                count++;
                if(count > max_decrease)
                    max_decrease = count;
            }else
            {
                count = 0;
            }
        }
        return (max_decrease + 1) / size;
    }

    public static List<Double> get_difference(List<Double> input)
    {
        ArrayList<Double> difference = new ArrayList<>();

        for (int d = 0; d < input.size()-1; d++) {
            double time1 = input.get(d);
            double time2 = input.get(d + 1);
            double timeDifference = time2 - time1;
            difference.add(timeDifference);
        }
        return difference;
    }

    public static double trapezoidRule (int size, double[] x, double[] y)
    {
        double sum = 0.0, increment;

        for ( int k = 1; k < size; k++ )
        {
            increment = 0.5 * (x[k]-x[k-1]) * (Math.abs(y[k])+Math.abs(y[k-1]));
            sum += increment;
        }
        return sum;
    }

    public static double get_sum(List<Double> input) {
        double sum = 0;
        for (double i: input) {
            sum += i;
        }
        return sum;
    }

    public static double auc(List<Double> x, List<Double>y)
    {
        int size = x.size();
        int direction = 1;
        List<Double> x_diff = get_difference(x);
        int diff_size = x_diff.size();
        int counter = 0;
        for (int i = 0; i < diff_size; i++) {
            if (x_diff.get(i) < 0) {
                counter++;
            }
        }
        if(counter == diff_size)
            direction = -1;

        if(counter > 0 & counter < diff_size)
            System.out.println("Error: x is neither increasing nor decreasing");
        double [] array_x = x.stream().mapToDouble(Double::doubleValue).toArray();
        double [] array_y = y.stream().mapToDouble(Double::doubleValue).toArray();
        return direction * trapezoidRule(size, array_x, array_y);
    }

    public static double[] get_bearing(List<Double> bearing, double distance)
    {
        List<Double> diff_bear = new ArrayList<>();
        int size = bearing.size();
        for(int i=1;i<size;i++)
        {
            if(bearing.get(i)<90 & bearing.get(i-1)>270)
                diff_bear.add(bearing.get(i) + 360 - bearing.get(i - 1));
            else if(bearing.get(i)>270 & bearing.get(i-1)<90)
                diff_bear.add(bearing.get(i-1) + 360 - bearing.get(i));
            else
                diff_bear.add(bearing.get(i)-bearing.get(i-1));
        }
        double[] res = new double[3];
        res[0] = getAverage(diff_bear);
        res[1] = getMax(diff_bear);
        res[2] = get_sum(diff_bear)/distance;
        return res;
    }
    public static double get_dist(List<Double> input, List<Double> second)
    {
        return auc(second,input);
    }

    public static void process_data(Context context)
    {
        HashMap data = read_csv(context);
        List accuracy = (List) data.get("Accuracy");
        List bearing = (List) data.get("Bearing");
        List acceleration_x = (List) data.get("acceleration_x");
        List acceleration_y = (List) data.get("acceleration_y");
        List acceleration_z = (List) data.get("acceleration_z");
        List gyro_x = (List) data.get("gyro_x");
        List gyro_y = (List) data.get("gyro_y");
        List gyro_z = (List) data.get("gyro_z");
        List second = (List) data.get("second");
        List speed = (List) data.get("Speed");
//        System.out.println(Arrays.toString(accuracy.toArray()));
//        System.out.println(Arrays.toString(bearing.toArray()));
//        System.out.println(Arrays.toString(acceleration_x.toArray()));
//        System.out.println(Arrays.toString(acceleration_y.toArray()));
//        System.out.println(Arrays.toString(acceleration_z.toArray()));
//        System.out.println(Arrays.toString(gyro_z.toArray()));
//        System.out.println(Arrays.toString(gyro_y.toArray()));
//        System.out.println(Arrays.toString(gyro_z.toArray()));
//        System.out.println(Arrays.toString(gyro_z.toArray()));
//        System.out.println(Arrays.toString(second.toArray()));
//        System.out.println(Arrays.toString(speed.toArray()));

        List<Double> real_acc = calc_real_value(acceleration_x,acceleration_y,acceleration_z);
        List<Double> real_gyro = calc_real_value(gyro_x,gyro_y,gyro_z);
        List<Double> rotate_x = calc_rotate_x(acceleration_x,acceleration_y,acceleration_z);
        List<Double> rotate_y = calc_rotate_y(acceleration_x,acceleration_y,acceleration_z);

        double acc_mean = getAverage(real_acc);
        double acc_max = getMax(real_acc);
        double acc_iqr = getIQR(real_acc);
        double acc_increase =  get_max_consecutive_increase(real_acc);
        double acc_decrease = get_max_consecutive_decrease(real_acc);
        List<Double> acc_diff_list = get_difference(real_acc);
        double acc_mean_diff = getAverage(acc_diff_list);
        double acc_max_diff = getMax(acc_diff_list);
        double rotate_x_max = getMax(rotate_x);
        List<Double> rotate_diff_list_x = get_difference(rotate_x);
        double rotate_x_max_diff = getMax(rotate_diff_list_x);
        double rotate_x_dist = get_dist(gyro_x,second);
        double rotate_y_max = getMax(rotate_y);
        List<Double> rotate_diff_list_y = get_difference(rotate_y);
        double rotate_y_max_diff = getMax(rotate_diff_list_y);
        double rotate_y_dist = get_dist(gyro_y,second);
        double rotate_z_dist = get_dist(gyro_z,second);
        double gyro_mean = getAverage(real_gyro);
        double gyro_max = getMax(real_gyro);
        double gyro_iqr = getIQR(real_gyro);
        double gyro_increase =  get_max_consecutive_increase(real_gyro);
        double gyro_decrease = get_max_consecutive_decrease(real_gyro);
        double rad_dist = get_dist(real_gyro,second);
        double avg_gyro = rad_dist/(double)second.get(second.size()-1);
        double speed_mean = getAverage(speed);
        double speed_max = getMax(speed);
        double speed_iqr = getIQR(speed);
        double speed_increase =  get_max_consecutive_increase(speed);
        double speed_decrease = get_max_consecutive_decrease(speed);
        double distance = get_dist(speed,second);
        double avg_speed = distance/(double)second.get(second.size()-1);
        double bear_increase =  get_max_consecutive_increase(bearing);
        double bear_decrease = get_max_consecutive_decrease(bearing);
        double bear_mean_diff = get_bearing(bearing,distance)[0];
        double bear_max_diff = get_bearing(bearing,distance)[1];
        double bear_change_per_dist = get_bearing(bearing,distance)[2];
        double trip_len = (double)second.get(second.size()-1);
    }
}
