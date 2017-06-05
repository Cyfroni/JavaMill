package Mill;

public class MVC {

    //... Create model, view, and controller.  They are
    //    created once here and passed to the parts that
    //    need them so there is only one copy of each.
    public static void main(String[] args) {

        try {

            Model model = new Model();
            View view = new View(model);
            view.setVisible(true);
            Controller controller = new Controller(model, view);
            controller.startGame();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

