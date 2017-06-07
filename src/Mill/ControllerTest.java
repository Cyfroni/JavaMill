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
    public void testUnselect() {
        final int[] status1 = {
                1,          1,          1,
                    0,      0,      0,
                        0, -1,  0,
                0,  0,  0,     -1,  0,  0,
                        0,  0,  0,
                    -1,     -1,      0,
                0,          0,          0
        };
        final int[] status2 = {
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
    public void testSelect() {
        final int[] status1 = {
                1,          1,          1,
                    0,      0,      0,
                        0, -1,  0,
                0,  0,  0,     -1,  0,  1,
                        0,  0,  0,
                    -1,     -1,      0,
                0,          1,          0
        };
        final int[] status2 = {
                -1,        -1,          1,
                    -1,     1,      0,
                        0,  0,  0,
                -1, 1,  0,      0,  0,  0,
                        0,  0,  0,
                    0,      0,      0,
                1,          0,          0
        };
        int[] mouse = new int[3];
        int[] newStatusA, newStatusB;

        for(int i=0;i<9;i++) model.pawnPlaced();

        for(int i=0;i<24;i++)
        {
            newStatusA = status1.clone();
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
                    assertArrayEquals(status1, newStatusB);
                    assertEquals("", controller.message);
                }
                else if(i == 1 && j == 1)
                {
                    assertArrayEquals(status1, newStatusB);
                    assertEquals("", controller.message);
                }
                else if(i == 2 && j == 2)
                {
                    assertArrayEquals(status1, newStatusB);
                    assertEquals("", controller.message);
                }
                else if(i == 14 && j == 14)
                {
                    assertArrayEquals(status1, newStatusB);
                    assertEquals("", controller.message);
                }
                else if(i == 22 && j == 22)
                {
                    assertArrayEquals(status1, newStatusB);
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
        final int[] status1 = {
                1,          1,          1,
                    0,      0,      0,
                        0, -1,  0,
                0,  0,  0,     -1,  0,  1,
                        0,  0,  0,
                    -1,     -1,      0,
                0,          0,          0
        };
        final int[] status2 = {
                -1,        -1,          1,
                    -1,     1,      0,
                        0,  0,  0,
                -1, 1,  0,      0,  0,  0,
                        0,  0,  0,
                    0,      0,      0,
                1,          0,          0
        };

        for(int i=0;i<24;i++)
        {
            controller.message = "";
            model.reset();
            for(int q=0;q<5;q++) model.pawnPlaced();
            model.setStatus(status1.clone());
            controller.toNine(i,model.getStatus());
            if(i == 23) assertEquals(true, controller.message.endsWith("no"));
            else
            {
                if(status1[i] == 0 )
                {
                    assertEquals(1,model.getStatus()[i]);
                    assertEquals(true, controller.message.endsWith("yes"));
                }
                else
                {
                    assertEquals(status1[i],model.getStatus()[i]);
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

    public void testCheckEnemy() {
        final int[] status1 = {
                1,          1,          1,
                    0,      0,      0,
                        0, -1,  0,
                0,  0,  0,     -1,  0,  0,
                        0,  0,  0,
                    -1,     -1,      0,
                0,          0,          0
        };
        final int[] status2 = {
                -1,        -1,          1,
                    -1,     1,      0,
                        0,  0,  0,
                -1, 1,  0,      0,  0,  0,
                        0,  0,  0,
                    0,      0,      0,
                1,          0,          0
        };

        for(int i=0;i<24;i++)
        {
            controller.message = "";
            model.setStatus(status1.clone());
            controller.checkEnemy(i);
            assertEquals("",controller.message);
        }


    }

    public void testCapture() throws Exception {
    }
}