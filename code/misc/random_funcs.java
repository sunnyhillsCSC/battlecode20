import java.util.Arrays;

public class random_funcs {

    public static int[] manhatten_step(int x1, int x2, int y1, int y2) {
        int x_dif = x2 - x1;
        int y_dif = y2-y1;

        int[] action_arr = {0,0};

        if (x_dif>0) {
            action_arr[0]=1;
        }

        else if (x_dif<0) {
            action_arr[0]=-1;
        }

        else if (y_dif>0) {
            action_arr[1]=1;
        }

        else if (y_dif<0) {
            action_arr[1]=-1;
        }

        return action_arr;
    }

    public static void main(String[] args) {
        int [] current = {24, 40};
        int [] dest = {30,25};


        int [] update_arr;

        boolean arrived = false;

        while (!arrived) {

            update_arr = manhatten_step(current[0], dest[0], current[1],dest[1]);

            current[0]+=update_arr[0];
            current[1]+=update_arr[1];

            System.out.println(Arrays.toString(update_arr));

            arrived = Arrays.equals(current, dest);

        }

            System.out.println(Arrays.toString(current));
            System.out.println(Arrays.toString(dest));

    }
}
