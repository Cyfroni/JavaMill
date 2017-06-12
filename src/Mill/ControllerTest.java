package Mill;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class ControllerTest {

    private MockController controller;
    private Model model;
    private int tab[][];

    private final int[] status1 = {
            1,          1,          1,
                0,      0,      0,
                    0, -1,  0,
            0,  0,  0,     -1,  0,  0,
                    0,  0,  0,
                -1,     -1,      0,
            0,          0,          0
    };
    private final int[] status2 = {
            -1,        -1,          1,
                -1,     1,      0,
                    0,  0,  0,
            -1, 1,  0,      0,  0,  0,
                    0,  0,  0,
                0,      0,      0,
            1,          0,          0
    };
    private final int[] status3 = {
            1,          1,          1,
                0,      0,      0,
                    0, -1,  0,
            0,  0,  0,     -1,  0,  1,
                    0,  0,  0,
                -1,     -1,      0,
            0,          0,          0
    };
    private final int[] status4 = {
            1,          1,          1,
                0,      0,      0,
                    0, -1,  0,
            0,  0,  0,     -1,  0,  1,
                    0,  0,  0,
                -1,     -1,      0,
            0,          1,          0
    };
    private final int[] status5 = {
            -1,        -1,          -1,
                -1,     1,      0,
                    0,  0,  0,
            -1, 1,  0,      0,  0,  1,
                    0,  0,  0,
                0,      0,      0,
            1,          0,          0
    };

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
            MockView view = new MockView(model);
            controller = new MockController(model, view);
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.makeServer();
        tab = model.getTab();
    }

    @Test
    public void testUnselect() {
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
    public void testSelect() {
        int[] mouse = new int[3];
        int[] newStatusA, newStatusB;

        for(int i=0;i<9;i++) model.pawnPlaced();

        for(int i=0;i<24;i++)
        {
            newStatusA = status4.clone();
            model.setStatus(newStatusA);
            controller.unselect(i, newStatusA, tab, mouse);
            for(int j=0;j<24;j++)
            {
                controller.message = "";
                newStatusB = newStatusA.clone();
                model.setStatus(newStatusB);
                controller.select(j,newStatusB,tab,mouse);
                if(i == 0 && j == 9)
                {
                    assertArrayEquals(new int[] {0,1,1,0,0,0,0,-1,0,1,0,0,-1,0,1,0,0,0,-1,-1,0,0,1,0}, newStatusB);
                    controller.checkEnemy(j);
                    assertEquals(true, controller.message.endsWith("yes"));
                }
                else if(i == 1 && j == 4)
                {
                    assertArrayEquals(new int[] {1,0,1,0,1,0,0,-1,0,0,0,0,-1,0,1,0,0,0,-1,-1,0,0,1,0}, newStatusB);
                    controller.checkEnemy(j);
                    assertEquals(true, controller.message.endsWith("yes"));
                }
                else if(i == 14 && j == 13)
                {
                    assertArrayEquals(new int[] {1,1,1,0,0,0,0,-1,0,0,0,0,-1,1,0,0,0,0,-1,-1,0,0,1,0}, newStatusB);
                    controller.checkEnemy(j);
                    assertEquals(true, controller.message.endsWith("yes"));
                }
                else if(i == 14 && j == 23)
                {
                    assertArrayEquals(new int[] {1,1,1,0,0,0,0,-1,0,0,0,0,-1,0,0,0,0,0,-1,-1,0,0,1,1}, newStatusB);
                    controller.checkEnemy(j);
                    assertEquals(true, controller.message.endsWith("yes"));
                }
                else if(i == 22 && j == 21)
                {
                    assertArrayEquals(new int[] {1,1,1,0,0,0,0,-1,0,0,0,0,-1,0,1,0,0,0,-1,-1,0,1,0,0}, newStatusB);
                    controller.checkEnemy(j);
                    assertEquals(true, controller.message.endsWith("yes"));
                }
                else if(i == 22 && j == 23)
                {
                    assertArrayEquals(new int[] {1,1,1,0,0,0,0,-1,0,0,0,0,-1,0,1,0,0,0,-1,-1,0,0,0,1}, newStatusB);
                    controller.checkEnemy(j);
                    assertEquals(true, controller.message.endsWith("no"));
                }
                else if(i == 0 && j == 0)
                {
                    assertArrayEquals(status4, newStatusB);
                    assertEquals("", controller.message);
                }
                else if(i == 1 && j == 1)
                {
                    assertArrayEquals(status4, newStatusB);
                    assertEquals("", controller.message);
                }
                else if(i == 2 && j == 2)
                {
                    assertArrayEquals(status4, newStatusB);
                    assertEquals("", controller.message);
                }
                else if(i == 14 && j == 14)
                {
                    assertArrayEquals(status4, newStatusB);
                    assertEquals("", controller.message);
                }
                else if(i == 22 && j == 22)
                {
                    assertArrayEquals(status4, newStatusB);
                    assertEquals("", controller.message);
                }
                else
                {
                    assertArrayEquals(newStatusA, newStatusB);
                    assertEquals("", controller.message);
                }
            }
        }

        for(int i=0;i<24;i++)
        {
            newStatusA = status2.clone();
            model.setStatus(newStatusA);
            controller.unselect(i, newStatusA, tab, mouse);
            for(int j=0;j<24;j++)
            {
                controller.message = "";
                newStatusB = newStatusA.clone();
                model.setStatus(newStatusB);
                controller.select(j,newStatusB,tab,mouse);
                if(i == 2 && j == 14)
                {
                    assertArrayEquals(new int[] {-1,-1,0,-1,1,0,0,0,0,-1,1,0,0,0,1,0,0,0,0,0,0,1,0,0}, newStatusB);
                    controller.checkEnemy(j);
                    assertEquals(true, controller.message.endsWith("yes"));
                }
                else if(i == 4 && j == 5)
                {
                    assertArrayEquals(new int[] {-1,-1,1,-1,0,1,0,0,0,-1,1,0,0,0,0,0,0,0,0,0,0,1,0,0}, newStatusB);
                    controller.checkEnemy(j);
                    assertEquals(true, controller.message.endsWith("yes"));
                }
                else if(i == 4 && j == 7)
                {
                    assertArrayEquals(new int[] {-1,-1,1,-1,0,0,0,1,0,-1,1,0,0,0,0,0,0,0,0,0,0,1,0,0}, newStatusB);
                    controller.checkEnemy(j);
                    assertEquals(true, controller.message.endsWith("yes"));
                }
                else if(i == 10 && j == 11)
                {
                    assertArrayEquals(new int[] {-1,-1,1,-1,1,0,0,0,0,-1,0,1,0,0,0,0,0,0,0,0,0,1,0,0}, newStatusB);
                    controller.checkEnemy(j);
                    assertEquals(true, controller.message.endsWith("yes"));
                }
                else if(i == 10 && j == 18)
                {
                    assertArrayEquals(new int[] {-1,-1,1,-1,1,0,0,0,0,-1,0,0,0,0,0,0,0,0,1,0,0,1,0,0}, newStatusB);
                    controller.checkEnemy(j);
                    assertEquals(true, controller.message.endsWith("yes"));
                }
                else if(i == 21 && j == 22)
                {
                    assertArrayEquals(new int[] {-1,-1,1,-1,1,0,0,0,0,-1,1,0,0,0,0,0,0,0,0,0,0,0,1,0}, newStatusB);
                    controller.checkEnemy(j);
                    assertEquals(true, controller.message.endsWith("yes"));
                }
                else if(i == 2 && j == 2)
                {
                    assertArrayEquals(status2, newStatusB);
                    assertEquals("", controller.message);
                }
                else if(i == 4 && j == 4)
                {
                    assertArrayEquals(status2, newStatusB);
                    assertEquals("", controller.message);
                }
                else if(i == 10 && j == 10)
                {
                    assertArrayEquals(status2, newStatusB);
                    assertEquals("", controller.message);
                }
                else if(i == 21 && j == 21)
                {
                    assertArrayEquals(status2, newStatusB);
                    assertEquals("", controller.message);
                }
                else
                {
                    assertArrayEquals(newStatusA, newStatusB);
                    assertEquals("", controller.message);
                }
            }
        }
    }

    @Test
    public void testToNine() {

        for(int i=0;i<24;i++)
        {
            controller.message = "";
            model.reset();
            for(int q=0;q<5;q++) model.pawnPlaced();
            model.setStatus(status3.clone());
            controller.toNine(i,model.getStatus());
            if(i == 23) assertEquals(true, controller.message.endsWith("no"));
            else
            {
                if(status3[i] == 0 )
                {
                    assertEquals(1,model.getStatus()[i]);
                    assertEquals(true, controller.message.endsWith("yes"));
                }
                else
                {
                    assertEquals(status3[i],model.getStatus()[i]);
                    assertEquals("", controller.message);
                }
            }
        }

        for(int i=0;i<24;i++)
        {
            controller.message = "";
            model.reset();
            for(int q=0;q<5;q++) model.pawnPlaced();
            model.setStatus(status2.clone());
            controller.toNine(i,model.getStatus());
            if(status2[i] == 0 )
            {
                assertEquals(1,model.getStatus()[i]);
                assertEquals(true, controller.message.endsWith("yes"));
            }
            else
            {
                assertEquals(status2[i],model.getStatus()[i]);
                assertEquals("", controller.message);
            }
        }
    }

    @Test
    public void testCheckEnemy() {

        for(int i=0;i<24;i++)
        {
            controller.message = "";
            model.setStatus(status1.clone());
            if (status1[i] == 1) {
                controller.checkEnemy(i);
                assertEquals("no", controller.message);
            }
            else assertEquals("",controller.message);
        }

        for(int i=0;i<24;i++)
        {
            controller.message = "";
            model.setStatus(status2.clone());
            if (status2[i] == 1) {
                controller.checkEnemy(i);
                assertEquals("yes", controller.message);
            }
            else assertEquals("",controller.message);
        }


    }

    @Test
    public void testCapture() {
        ArrayList<Integer> expectedToSendYes = new ArrayList<>();
        expectedToSendYes.add(0);
        expectedToSendYes.add(1);
        expectedToSendYes.add(2);
        expectedToSendYes.add(3);
        expectedToSendYes.add(9);

        for(int j=0;j<2;j++) {
            for (int i = 0; i < 24; i++) {
                controller.message = "";
                model.setStatus(status5.clone());
                controller.capture(i, model.getStatus());
                if (i==3 && j==1) assertEquals("endG",controller.message);
                else if (expectedToSendYes.contains(i)) {
                    assertEquals("yes", controller.message);
                }
                else assertEquals("", controller.message);
            }
            for(int i=0;i<9;i++) model.pawnPlaced();
        }

        expectedToSendYes.clear();
        expectedToSendYes.add(7);
        expectedToSendYes.add(12);
        expectedToSendYes.add(18);
        expectedToSendYes.add(19);
        model.reset();
        for(int j=0;j<2;j++) {
            for (int i = 0; i < 24; i++) {
                controller.message = "";
                model.setStatus(status4.clone());
                controller.capture(i, model.getStatus());
                if (expectedToSendYes.contains(i)) {
                    assertEquals("yes", controller.message);
                }
                else assertEquals("", controller.message);
            }
            for(int i=0;i<9;i++) model.pawnPlaced();
        }
    }
}