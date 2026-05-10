package model;

import controller.StartMenuController;
import view.StartMenu;

public class Game {
    public static void main(String[] args) {
        StartMenu sm = new StartMenu();
        new StartMenuController(sm);
    }
}
