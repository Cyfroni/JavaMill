package Mill;

import org.junit.Before;
import org.junit.Test;


import java.util.ArrayList;

import static org.junit.Assert.*;

public class ModelTest  {

    private Model model;

    @Before
    public void setUp() {
        model = new Model();
    }

    @Test
    public void testGetStatusSerialized() {

        int[] status1 = {
                1,          1,          1,
                0,      0,      0,
                1,  0,  0,
                -1, 0,  0,      0,  0,  0,
                0,  1,  0,
                0,     -1,      0,
                -1,        -1,         -1
        };
        int[] status2 = {
                1,          -1,          0,
                0,      0,      0,
                -1,  1,  0,
                -1, 0,  1,      0,  1,  0,
                0,  -1,  0,
                1,     -1,      0,
                -1,        0,         -1
        };

        model.setStatus(status1);
        String serializedStatus = model.getStatusSerialized();
        assertEquals("-1-1-1000-1001000000-10010111", serializedStatus);

        model.setStatus(status2);
        serializedStatus = model.getStatusSerialized();
        assertEquals("-1100001-1010-10-10010-110101", serializedStatus);

    }

    @Test
    public void testDeserializeStatus() {
        String newStatus = "-11100100100110100100-1-1-10";
        model.deserializeStatus(newStatus);
        int[] status = model.getStatus();

        for(int i=0;i<24;i++)
        {
            assertEquals(true,newStatus.startsWith(Integer.toString(status[i])));
            newStatus = newStatus.substring(Integer.toString(status[i]).length(),newStatus.length());
        }

        newStatus = "11-10110-11101-1010001000-10";
        model.deserializeStatus(newStatus);
        status = model.getStatus();

        for(int i=0;i<24;i++)
        {
            assertEquals(true,newStatus.startsWith(Integer.toString(status[i])));
            newStatus = newStatus.substring(Integer.toString(status[i]).length(),newStatus.length());
        }

    }

    @Test
    public void testCheckMill(){
        int[] status = {
                1,          1,          1,
                    0,      0,      0,
                        1,  0,  0,
                -1, 0,  0,      0,  0,  0,
                        0,  1,  0,
                    0,     -1,      0,
                -1,        -1,         -1
        };
        ArrayList<Integer> expectedToBeTrue = new ArrayList<>();
        expectedToBeTrue.add(0);
        expectedToBeTrue.add(1);
        expectedToBeTrue.add(2);
        int[] status2 = {
                1,          1,          0,
                    0,      0,      0,
                        1,  0,  0,
                -1, 0,  0,      0,  0,  0,
                        0,  1,  0,
                    0,     -1,      0,
                -1,        -1,         -1
        };

        for(int i=0;i<24;i++)
        {
            model.setStatus(status);
            assertEquals(expectedToBeTrue.contains(i), model.checkMill(i));
            model.setStatus(status2);
            assertEquals(false, model.checkMill(i));
        }

        for(int i=0;i<9;i++) model.pawnPlaced();

        for(int i=0;i<24;i++)
        {
            model.setStatus(status);
            assertEquals(expectedToBeTrue.contains(i), model.checkMill(i));
            model.setStatus(status2);
            assertEquals(false, model.checkMill(i));
        }

    }

    @Test
    public void testCheckIfEnemyCanMove() {

        int[] status = {
                1,          1,          1,
                0,      0,      0,
                0, -1,  0,
                0,  0,  0,     -1,  0,  0,
                0,  0,  0,
                -1,     -1,      0,
                0,          0,          0
        };
        int[] status2 = {
                -1,        -1,          1,
                -1,     1,      0,
                0,  0,  0,
                -1, 1,  0,      0,  0,  0,
                0,  0,  0,
                0,      0,      0,
                1,          0,          0
        };

        model.setStatus(status);
        assertEquals(true, model.checkIfEnemyCanMove());
        model.setStatus(status2);
        assertEquals(true, model.checkIfEnemyCanMove());

        for(int i=0;i<9;i++) model.pawnPlaced();

        model.setStatus(status);
        assertEquals(true, model.checkIfEnemyCanMove());
        model.setStatus(status2);
        assertEquals(false, model.checkIfEnemyCanMove());
    }

}