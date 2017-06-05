package Mill;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


import java.io.IOException;

public class ControllerTest {

    private MockController controller;
    private Model model;
    private MockView view;
    private int tab[][];

    private class MockView extends View {
        boolean updateViewCalled = false;

        MockView(Model model) throws IOException {
            super(model);
        }

        @Override
        public void update() {
            updateViewCalled = true;
        }
    }

    ;

    private class MockController extends Controller {
        String message;

        MockController(Model model, View view) throws InterruptedException {
            super(model, view);
        }

        @Override
        public void sendMessage(String message) {
            this.message = message;
        }
    }


    @Before
    public void setUp() {
        model = new Model();
        model.setConnection(true);
        try {
            view = new MockView(model);
            controller = new MockController(model, view);
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.makeServer();
        tab = model.getTab();
    }

    @Test
    public void testUnselect() throws Exception {
        int[] status1 = {
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
        int[] mouse = new int[3];
        int[] newStatus1 = status1.clone();
        int[] newStatus2 = status2.clone();

        for(int i=0;i<9;i++) model.pawnPlaced();

        for(int i=0;i<24;i++)
        {
            controller.message = "";
            controller.unselect(i,newStatus1,tab,mouse);
            assertEquals("",controller.message);
            if (status1[i]==1) assertEquals(2,newStatus1[i]);
            else assertEquals(status1[i],newStatus1[i]);

            controller.message = "";
            controller.unselect(i,newStatus2,tab,mouse);
            assertEquals("",controller.message);
            if (status2[i]==1) assertEquals(2,newStatus2[i]);
            else assertEquals(status2[i],newStatus2[i]);
        }


    }

    @Test
    public void testSelect() throws Exception {
        int[] status1 = {
                1,          1,          1,
                    0,      0,      0,
                        0, -1,  0,
                0,  0,  0,     -1,  0,  1,
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
        int[] mouse = new int[3];
        int[] newStatus1, newStatus11;
        int[] newStatus2, newStatus22;

        for(int i=0;i<9;i++) model.pawnPlaced();

        for(int i=0;i<24;i++)
        {
            newStatus1 = status1.clone();
            model.setStatus(newStatus1);
            controller.unselect(i, newStatus1, tab, mouse);
            for(int j=0;j<24;j++)
            {
                newStatus11 = newStatus1.clone();
                model.setStatus(newStatus11);
                controller.select(j,newStatus11,tab,mouse);
                if(i == 0 && j == 9)        assertArrayEquals(new int[] {0,1,1,0,0,0,0,-1,0,1,0,0,-1,0,1,0,0,0,-1,-1,0,0,0,0}, newStatus11);
                else if(i == 1 && j == 4)   assertArrayEquals(new int[] {1,0,1,0,1,0,0,-1,0,0,0,0,-1,0,1,0,0,0,-1,-1,0,0,0,0}, newStatus11);
                else if(i == 14 && j == 13) assertArrayEquals(new int[] {1,1,1,0,0,0,0,-1,0,0,0,0,-1,1,0,0,0,0,-1,-1,0,0,0,0}, newStatus11);
                else if(i == 14 && j == 23) assertArrayEquals(new int[] {1,1,1,0,0,0,0,-1,0,0,0,0,-1,0,0,0,0,0,-1,-1,0,0,0,1}, newStatus11);
                else if(i == 0 && j == 0) assertArrayEquals(status1, newStatus11);
                else if(i == 1 && j == 1) assertArrayEquals(status1, newStatus11);
                else if(i == 2 && j == 2) assertArrayEquals(status1, newStatus11);
                else if(i == 14 && j == 14) assertArrayEquals(status1, newStatus11);
                else assertArrayEquals(newStatus1, newStatus11);

            }
        }



    }

    public void testToNine() throws Exception {

    }

    public void testCheckEnemy() throws Exception {
    }

    public void testCapture() throws Exception {
    }
}