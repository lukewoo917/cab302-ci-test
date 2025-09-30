package com.example.demo.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;


/**
 * Routes between FXML views. Can maintain state between views.
 */
public final class NavigationManager {
    private static Stage stage;
    private static Scene scene;

    private static final Deque<Parent> backStack = new ArrayDeque<>();
    private static Parent currentRoot;

    private static final String BASE_PATH = "/com/example/demo/";
    private static final Map<String, Parent> cacheRoots = new HashMap<>();
    private static final Map<String, Object> cacheControllers = new HashMap<>();

    private NavigationManager() {}

    /**
     * Initializes NavigationManager.
     * @param s the stage to use for the Scene.
     * @param initialRoot the root node of the Scene.
     * @param width the width of the Scene.
     * @param height the height of the Scene.
     */
    public static void init(Stage s, Parent initialRoot, double width, double height) {
        stage = s;
        scene = new Scene(initialRoot, width, height);
        scene.getStylesheets().add(NavigationManager.class.getResource("/com/example/demo/stylesheet.css").toExternalForm());
        currentRoot = initialRoot;
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Checks for a previous view on the back stack.
     * @return true if there is a previous view to go back to.
     */
    public static boolean canBack() {
        return !backStack.isEmpty();
    }

    /**
     * Navigates back to the previous view if there is one. Will otherwise do nothing! Check for canBack() first.
     */
    public static void back() {
        if (!canBack()) return;
        Parent prev = backStack.pop();
        currentRoot = prev;
        scene.setRoot(prev);
    }

    /**
     * Navigates to the given view. Views are given as a string e.g. "main-view.fxml".
     * @param fxmlPath the path to the FXML view file. e.g. "main-view.fxml"
     */
    public static void goTo(String fxmlPath) {
        if (stage == null) throw new IllegalStateException("NavigationManager not initialized");
        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(BASE_PATH + fxmlPath));
            Parent nextRoot = loader.load();
            pushAndShow(nextRoot);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load: " + fxmlPath, e);
        }
    }

    /**
     * Preloads a view for later use. Views are given as a string path.
     * @param key the key to use for the view. e.g. "main-view"
     * @param fxmlPath the path to the FXML view file. e.g. "main-view.fxml"
     */
    public static void preload(String key, String fxmlPath) {
        if (cacheRoots.containsKey(key)) return;
        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(BASE_PATH + fxmlPath));
            Parent root = loader.load();
            cacheRoots.put(key, root);
            cacheControllers.put(key, loader.getController());
        } catch (IOException e) {
            throw new RuntimeException("Failed to preload: " + fxmlPath, e);
        }
    }

    /**
     * Navigates to a previously preloaded view.
     * @param key the key of the view to navigate to.
     */
    public static void goToCached(String key) {
        Parent root = cacheRoots.get(key);
        if (root == null) throw new IllegalStateException("No cached view for key: " + key + ". Call preload first.");
        pushAndShow(root);
    }

    /**
     * Gets a previously preloaded controller.
     * @param key the key of the controller to get.
     * @return the controller or null if it doesn't exist.
     */
    public static Object getCachedController(String key) {
        return cacheControllers.get(key);
    }

    /**
     * Clears the back stack.
     */
    public static void clearHistory() {
        backStack.clear();
    }

    /**
     * Pushes the given view onto the back stack and shows it.
     * @param nextRoot the view to push and show.
     */
    private static void pushAndShow(Parent nextRoot) {
        if (currentRoot != null) backStack.push(currentRoot);
        currentRoot = nextRoot;
        if (scene == null) {
            scene = new Scene(nextRoot); // fallback
            stage.setScene(scene);
        } else {
            scene.setRoot(nextRoot); // keep same Scene = preserves window, accelerators, etc.
        }
    }
}