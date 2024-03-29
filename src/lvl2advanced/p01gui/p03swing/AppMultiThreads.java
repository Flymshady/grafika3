package lvl2advanced.p01gui.p03swing;

import static org.lwjgl.glfw.GLFW.GLFW_COCOA_RETINA_FRAMEBUFFER;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWaitEvents;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.Platform;

import lvl2advanced.p01gui.p01simple.Renderer;
import lvl2advanced.p01gui.p02threads.LwjglWindowThread;

public class AppMultiThreads {

	private Frame testFrame;
	private int demoId = 1;
	static String[] names = { "lvl2advanced.p01gui.p01simple", "lvl5others.p02fractal.Renderer" };
	static int[] countMenuItems = { 1, 1 };
	static String[] nameMenuItem = { "basic", "advanced" };

	LwjglWindowThread thread;
	AppWindow mainWindow;
	CountDownLatch quit;
	long window = 0;
	
	private void makeGUI(Frame testFrame) {
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				demoId = Integer
						.valueOf(ae.getActionCommand().substring(0, ae.getActionCommand().lastIndexOf('-') - 1).trim());
				//setApp(testFrame, names[demoId - 1]);
			}
		};

		MenuBar menuBar = new MenuBar();
		int menuIndex = 0;
		for (int itemMenu = 0; itemMenu < nameMenuItem.length; itemMenu++) {
			Menu menu1 = new Menu(nameMenuItem[itemMenu]);
			MenuItem m;
			for (int i = 0; i < names.length && i < countMenuItems[itemMenu]; i++) {
				m = new MenuItem(new Integer(menuIndex + 1).toString() + " - " + names[menuIndex]);
				m.addActionListener(actionListener);
				menu1.add(m);
				menuIndex++;
			}
			menuBar.add(menu1);
		}

		/*
		 * keyAdapter = new KeyAdapter() {
		 * 
		 * @Override public void keyPressed(KeyEvent e) { if ((e.getModifiers()
		 * & KeyEvent.ALT_MASK) != 0) { switch (e.getKeyCode()) { case
		 * KeyEvent.VK_HOME: demoId = 1; setApp(testFrame, names[demoId - 1]);
		 * 
		 * break; case KeyEvent.VK_END: demoId = names.length; setApp(testFrame,
		 * names[demoId - 1]); break; case KeyEvent.VK_LEFT: if (demoId > 1)
		 * demoId--; setApp(testFrame, names[demoId - 1]); break; case
		 * KeyEvent.VK_RIGHT: if (demoId < names.length) demoId++;
		 * setApp(testFrame, names[demoId - 1]); break; } } }
		 * 
		 * };
		 */
		testFrame.setMenuBar(menuBar);
		testFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				new Thread() {
					@Override
					public void run() {
						// if (animator.isStarted())
						// animator.stop();
						System.exit(0);
					}
				}.start();
			}
		});

		// testFrame.setTitle(ren.getClass().getName());

		testFrame.pack();
		testFrame.setVisible(true);
		
	}

	private Integer setApp() {
		if (thread != null) {
			//glfwWaitEvents();
			//quit.countDown();

			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			 
			
			thread.dispose();
			// make new window with renderer in new thread
		}
		quit = new CountDownLatch(1);
		thread = new LwjglWindowThread(0, quit, new lvl2advanced.p01gui.p01simple.Renderer());
		window = thread.getWindow();
		thread.start();
		return null;
	}

	public void startApp() {
		try {
			testFrame = new Frame("TestFrame");
			testFrame.setSize(512, 384);

			// System.out.println("searching all samples");

			// getDemoNames("lvl", "\\Renderer.class"); // comment this to have
			// only the samples listed in names

			makeGUI(testFrame);

			//setApp(testFrame, names[0]);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() {
		SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	            mainWindow = new AppWindow(new Callable<Integer>() {
	            	   public Integer call() {
	            	        return setApp();
	            	   }
	            	});
	            mainWindow.setVisible(true);
	        }
		});
		
		// common part for all GLFW windows

		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are
									// already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden
													// after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be
													// resizable
		if (Platform.get() == Platform.MACOSX) {
			glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE);
		}

		setApp();
		

		
		/*
		// make new window with renderer in new thread
		thread = new LwjglWindowThread(0, quit, new lvl2advanced.p01gui.p01simple.Renderer());
		window = thread.getWindow();
		thread.start();
		
		System.out.println(window);
		*/
		
		// waiting for finishing
		out: while (true) {
			glfwWaitEvents();

			if (window > 0 && glfwWindowShouldClose(window)) {
				quit.countDown();
				break out;
			}
		}

		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		thread.dispose();
		
		//mainWindow.dispatchEvent(new WindowEvent(mainWindow, WindowEvent.WINDOW_CLOSING));
		System.out.println("App end");

		// Terminate GLFW and free the error callback
		glfwTerminate();
		Objects.requireNonNull(glfwSetErrorCallback(null)).free();
		
		mainWindow.setVisible(false); // hide window
		mainWindow.dispose(); // close window
		System.exit(0); // stop program
	}

	public static void main(String[] args) {
		new AppMultiThreads().start();
		    
	}

}